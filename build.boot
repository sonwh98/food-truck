(set-env!
 ;; :offline? true
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs          "1.7.170-3"   :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.0"      :scope "test"]
                 [com.cemerick/piggieback "0.2.1"  :scope "test"]
                 [weasel                  "0.7.0"  :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [adzerk/boot-reload        "0.4.2"      :scope "test"]
                 [pandeiro/boot-http        "0.7.1-SNAPSHOT"      :scope "test"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [reagent "0.5.1"]
                 [org.clojure/core.logic "0.8.10"]

                 
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.taoensso/timbre "4.1.4"]
                 [com.stuartsierra/component "0.2.3"]
                 [org.danielsz/system "0.1.9"]
                 [environ "1.0.0"]
                 [boot-environ "1.0.1"]
                 
                 [org.codehaus.groovy/groovy-all "2.4.5"]
                 [com.datomic/datomic-free "0.9.5327" :exclusions [joda-time]]
                 [datomic-schema "1.3.0"]

                 [ring/ring-core "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [http-kit "2.1.19"]
                 [jarohen/chord "0.6.0"]

                 [com.cognitect/transit-clj "0.8.285"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [neo-matrix "0.1.3-SNAPSHOT"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[food-truck.server.main :as m]
 '[boot.core            :as core]
 '[environ.core :refer [env]]
 '[environ.boot :refer [environ]])

(deftask build-cljs []
  (comp (speak)
        (cljs)))

(deftask run []
  (comp 
   (watch)
   (cljs-repl)
   (reload)
   (build-cljs)))

(deftask development []
  (task-options! reload {:asset-path "public"
                         :on-jsload 'food-truck.client.menu/on-js-reload})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))

(deftask build-all []
  (comp (aot :namespace '#{food-truck.server.main food-truck.server.db food-truck.server.ws food-truck.server.restaurant
                           food-truck.messaging food-truck.transit})
        (cljs)))

;;boot environ -e db-url="datomic:free://localhost:4334/four" start
;;boot repl -c 
(deftask start []
  (comp (aot :namespace '#{food-truck.server.main food-truck.server.db food-truck.server.ws food-truck.server.restaurant
                           food-truck.messaging food-truck.transit})

        (with-pre-wrap fileset
          (println "db-url=" (env :db-url))
          (food-truck.server.main/-main)
          fileset)
        (development)
        (watch)
        (cljs-repl)
        (reload)
        (speak)
        (cljs)))

(deftask build-jar []
  (comp (aot :namespace '#{food-truck.server.main food-truck.server.db food-truck.server.ws food-truck.server.restaurant
                           food-truck.messaging food-truck.transit})
        (cljs)
        (pom :project 'food-truck
             :version "0.1.0")
        (uber)
        (jar :main 'food-truck.server.main))
  )


