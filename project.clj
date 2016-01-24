(defproject food-truck "0.1.0-SNAPSHOT"
  :dependencies [[adzerk/boot-cljs "1.7.170-3" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.0" :scope "test"]
                 [com.cemerick/piggieback "0.2.1" :scope "test"]
                 [weasel "0.7.0" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.12" :scope "test"]
                 [adzerk/boot-reload "0.4.2" :scope "test"]
                 [pandeiro/boot-http "0.7.1-SNAPSHOT" :scope "test"]
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
                 [com.kaicode/morpheus "0.1.2-SNAPSHOT"]
                 [com.kaicode/mercury "0.1.1-SNAPSHOT"]
                 [com.kaicode/teleport "0.1.1-SNAPSHOT"]
                 [com.kaicode/wocket "0.1.1-SNAPSHOT"]
                 [com.kaicode/tily "0.1.1-SNAPSHOT"]
                 [com.kaicode/tweenie "0.1.1-SNAPSHOT"]
                 [jarohen/chord "0.7.0"]
                 [domina "1.0.3"]]
  :source-paths
  ["src"])