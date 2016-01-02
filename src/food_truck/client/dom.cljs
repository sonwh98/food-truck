(ns food-truck.client.dom
  (:require [food-truck.messaging :as m]
            [cljs.core.async :refer [put! chan]]))

(def window js/window)
(def document js/document)

(.. document (addEventListener "DOMContentLoaded" #(m/broadcast [:dom/ready true])))

(.. window (addEventListener "resize" #(m/broadcast [:window/resize true]) false))

(defn by-id [id]
  (.. document (getElementById id)))


(defn event->chan [element event-str]
  (let [event-chan (chan 1)]
    (.. element (addEventListener event-str (fn [event]
                                              (put! event-chan event))))
    event-chan))

(defn on [element event-str call-back-fn]
  (.. element (addEventListener event-str call-back-fn)))

(defn str->dom-element [html-str]
  (let [div (.. document (createElement "div"))]
    (set! (.. div -innerHTML) html-str)
    (.. div -firstChild)))

(def whenever-dom-ready (m/whenever :dom/ready :broadcasted))
