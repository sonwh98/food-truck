(ns food-truck.server.db
  (:require [datomic.api :as d]
            [reloaded.repl :refer [system]]
            [environ.core :refer [env]]))

(defn get-url []
  (or (env :db-url)  "datomic:mem://food-truck"))

(defn get-conn []
  (:conn  (:datomic-db system)))

(defn get-db []
  (d/db (get-conn)))

(defn transact [datoms]
  (d/transact (get-conn) datoms))

(defn q
  "wrapper around d/q so that you don't have to pass in the current database"
  [& params]
  (let [query (first params)
        query+db [query (get-db)]
        variable-bindings (rest params)
        params (vec (concat query+db variable-bindings))]
    (apply d/q params)))


(defn create-test-db []
  ;;(set! *data-readers* (.getRawRoot #'*data-readers*))
  (let [schema (-> "resources/schema.edn" slurp read-string )
        test-data (-> "resources/qt-sandwich.edn" slurp read-string)]
    (transact schema)
    (transact test-data)
    ))
