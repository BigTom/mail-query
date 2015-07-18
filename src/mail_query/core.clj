(ns mail-query.core
  (:require [mail-query.view :refer [results-page not-found-page]])
  (:require [org.httpkit.server :as http-kit]
            [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]]
            [compojure.core :refer [defroutes ANY GET PUT POST DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [org.httpkit.client :as http]
            [cheshire.core :refer [parse-string]])
  (:gen-class))

(def mailgun-domain (System/getenv "MAILGUN_DOMAIN"))
(def mailgun-api-key (System/getenv "MAILGUN_API_KEY"))
(def mailgun-admin-pwd (System/getenv "MAILGUN_ADMIN_PWD"))

(def resource (str "https://api.mailgun.net/v3/" mailgun-domain "/events"))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Mailgun Log Reader")))

(defn find-email [email delivered-only]
  (let [params (if delivered-only {:recipient email :event "delivered"} {:recipient email})
        options {:timeout      2000                         ; ms
                 :limit        10
                 :basic-auth   ["api" mailgun-api-key]
                 :query-params params
                 :user-agent   "User-Agent-string"}
        {:keys [body error]} @(http/get resource options)]
    (if error
      (response (str "Failed, exception: " error))
      (results-page (parse-string body)))))

(defn handle-log-query [req]
  (let [email (get-in req [:params "recipient"])
        delivered-only (= "on" (get-in req [:params "delivered-only"]))]
    (find-email email delivered-only)))

(defroutes routes
           (GET "/find" [] handle-log-query)
           (GET "/" [] handle-log-query)
           (not-found (not-found-page)))

(defn authenticated? [name pass]
  (and (= name "admin")
       (= pass mailgun-admin-pwd)))

(def app
  (-> routes
      (wrap-params)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-basic-authentication authenticated?)
      (wrap-server)))

(defn -main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "8080"))]
    (http-kit/run-server app
                         {:port port})))

(defn -dev-main []
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (http-kit/run-server (wrap-reload #'app)
                         {:port port})))


