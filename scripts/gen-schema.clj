#!/usr/bin/env boot

(set-env! :dependencies '[[org.clojure/clojure "1.7.0"]
                          [com.datomic/datomic-free "0.9.5327" :exclusions [joda-time]]
                          [datomic-schema "1.3.0"]
                          ])

(require '[datomic-schema.schema :as s])
(use 'clojure.pprint)

(def schema
  [(s/schema element
             (s/fields
              [name :string :indexed]
              [weight :string :indexed]
              [symbol :string :indexed :unique-value]))])


(pprint (s/generate-schema schema))
