#!/usr/bin/env boot

(set-env!
 :source-paths    #{"src/food-truck/server"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                          [com.datomic/datomic-free "0.9.5327" :exclusions [joda-time]]
                          ])

