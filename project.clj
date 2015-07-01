(defproject mail-query "1.0.0"
  :description "Mailgun event query web app"
  :url "https://github.com/BigTom"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.18"]
                 [ring "1.3.2"]
                 [compojure "1.3.4"]
                 [hiccup "1.0.5"]
                 [cheshire "5.4.0"]
                 [clj-time "0.9.0"]]

  :min-lein-version "2.0.0"

  :uberjar-name "mail-query.jar"

  :main mail-query.core

  :profiles {:dev
             {:main mail-query.core/-dev-main}})
