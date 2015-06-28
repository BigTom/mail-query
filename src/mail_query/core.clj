(ns mail-query.core
  (:require [mail-query.view :refer [results-page]])
  (:require [org.httpkit.server :as http-kit]
            [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET PUT POST DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [org.httpkit.client :as http]
            [cheshire.core :refer [parse-string]])
  (:gen-class))

(def mailgun-domain (System/getenv "MAILGUN_DOMAIN"))
(def mailgun-api-key (System/getenv "MAILGUN_API_KEY"))

(def resource (str "https://api.mailgun.net/v3/" mailgun-domain "/events"))

(defn wrap-server [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "Mailgun Log Reader")))

(defn wrap-bounce-favicon [handler]
  (fn [req]
    (if (= [:get "/favicon.ico"] [(:request-method req) (:uri req)])
      {:status 404
       :headers {}
       :body ""}
      (handler req))))

(defn find-email [email delivered-only]
  (let [params (if delivered-only {:recipient email :event "delivered"} {:recipient email})
        options {:timeout      2000   ; ms
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
           (GET "/find"  [] handle-log-query)
           (GET "/"      [] handle-log-query)
           (not-found "Page not found."))

(def app
  (-> routes
      (wrap-params)
      (wrap-bounce-favicon)
      (wrap-resource "static")
      (wrap-file-info)
      (wrap-server)))

(defn -main
  ([]
   (-main 80))
  ([port]
  (http-kit/run-server app
                       {:port (Integer. port)})))

(defn -dev-main
  ([]
  (-dev-main 3000))
  ([port]
   (http-kit/run-server (wrap-reload #'app)
                       {:port (Integer. port)})))


