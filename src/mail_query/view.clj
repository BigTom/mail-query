(ns mail-query.view
  (:require [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]]
            [clj-time.coerce :refer [from-long]]
            [clj-time.format :refer [formatter unparse]]))

(def custom-formatter (formatter "EEE dd/MMM/yyyy hh:mm:ss z"))

(defn query-form []
  (html
    [:form.pure-form
     {:method "GET" :action "/find"}
      [:label {:for :recipient}
       "Recipient email:"]
       [:input
        {:type "text"
         :name "recipient"
         :id   "recipient"
         :value ""}]
     [:input
      {:type    "checkbox"
       :name    "delivered-only"
       :id      "delivered-only"
       :checked "true"} "Show Delivered only"]
     [:button.pure-button.pure-button-primary
      {:type  "submit" } "Send"]]))

(defn results-page [{items "items"}]
  (html5 {:lang :en}
         [:head
          [:title "Mail Delivery"]
          [:meta {:name    :viewport
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:href "pure/pure-min.css"
                  :rel  :stylesheet}]]
         [:body
          [:div.pure-g
           [:h1.pure-u-1 "e-mail events"]
           [:div.pure-u-1
            (if (seq items)
              [:table.pure-table.pure-table-striped
               [:thead
                [:tr
                 [:th "Recipient"]
                 [:th "Event"]
                 [:th "Timestamp"]]]
               [:tbody
                (for [i items]
                  [:tr
                   [:td (h (get i "recipient"))]
                   [:td (h (get i "event"))]
                   [:td (h (unparse custom-formatter (from-long (long (* (get i "timestamp") 1000)))))]])]]
              ;                                                        * 1000 to get millisecond offset
              [:div.pure-u-1 "There are no items."])]
           [:div.pure-u-1
            [:h2 "New Query"]
            (query-form)]]]))

