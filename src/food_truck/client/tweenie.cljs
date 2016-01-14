(ns food-truck.client.tweenie
  (:require [food-truck.client.dom :as dom]
            [food-truck.client.layout :as layout]))

(enable-console-print!)

(defn additional-distance [start-val end-val fraction]
  (let [delta (- end-val start-val)]
    (if (>= fraction 1)
      delta
      (* fraction delta))))

(defn linear-easing [start-val, end-val, duration, t]
  (let [fraction-of-time (/ t duration)]
    (+ start-val (additional-distance start-val end-val fraction-of-time))))

(defn tween [config-map]
  (let [start-val (:from config-map)
        end-val (:to config-map)
        duration (:duration config-map)
        easing-fn (:easing-fn config-map)
        on-update (:on-update config-map)
        start-time (atom nil)
        tick-time (atom nil)]
    (fn [time]
      (if (nil? @start-time)
        (reset! start-time time))
      (let [delta-time (- time @start-time)
            val (easing-fn start-val end-val duration delta-time)]
        (if (<= delta-time duration)
          (on-update val))
        val))))

(defn animate [tween-fn]
  ((fn animation-loop [time]
     (tween-fn time)
     (js/requestAnimationFrame animation-loop)
     )))

(def t (tween {:from 1 :to 1000
               :duration 1000
               :easing-fn linear-easing
               :on-update (fn [val]
                            (let [s (dom/by-id "category-Sandwiches")]
                              (layout/position s val 0))
                            
                            )}))

(defn doit []
  (animate t))
