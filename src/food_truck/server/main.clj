(ns food-truck.server.main
  (:gen-class)
  (:require [org.httpkit.server :as http-kit]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [compojure.core :refer [defroutes]]
            [com.kaicode.wocket.server :as ws]
            [food-truck.server.restaurant]
            [food-truck.server.db :as db]
            [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [system.components
             [http-kit :refer [new-web-server]]
             [datomic :refer [new-datomic-db]]
             [repl-server :refer [new-repl-server]]]
            [reloaded.repl]))

(defroutes all-routes
           (route/resources "/"))

(defn dev-system []
  (component/system-map
    :datomic-db (new-datomic-db (db/get-url))
    ;:web (new-web-server (or (env :http-port) 8080) handler)
    :repl-server (new-repl-server 2222)))

(defn -main [& args]
  (http-kit/run-server ws/listen-for-client-websocket-connections {:port 9090})
  (http-kit/run-server (site #'all-routes) {:port 8080})
  (reloaded.repl/set-init! dev-system)
  (reloaded.repl/go)
  (println "started...")
  )
