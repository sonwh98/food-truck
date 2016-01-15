(ns food-truck.client.tweenie
  (:require [food-truck.client.dom :as dom]
            [food-truck.matrix :as matrix]
            [food-truck.client.layout :as layout]))

(enable-console-print!)

(defn additional-distance [start-val end-val fraction]
  (let [delta (- end-val start-val)]
    (if (>= fraction 1)
      delta
      (* fraction delta))))

(defn ease-linear [start-val end-val duration t]
  (let [fraction-of-time (/ t duration)]
    (+ start-val (additional-distance start-val end-val fraction-of-time))))

(defn ease-in [start-val end-val duration t]
  (let [fraction-of-time (/ t duration)
        fraction-of-time (js/Math.pow fraction-of-time 5)]
    (+ start-val (additional-distance start-val end-val fraction-of-time))))

(defn ease-out [start-val end-val duration t]
  (let [fraction-of-time (/ t duration)
        fraction-of-time (- 1
                            (js/Math.pow (- 1 fraction-of-time) 5))]
    (+ start-val (additional-distance start-val end-val fraction-of-time))))

(defmulti tween (fn [config-map]
                  (let [start-val (:from config-map)
                        type-start-val (type start-val)]
                    (cond
                      (= type-start-val cljs.core/PersistentVector) (if (= (-> start-val first type)
                                                                           js/Number)
                                                                      :vector
                                                                      :matrix)
                      (= type-start-val js/Number) :number))))

(defmethod tween :number [config-map]
  (let [start-val (:from config-map)
        end-val (:to config-map)
        duration (:duration config-map)
        easing-fn (:easing-fn config-map)
        on-update (:on-update config-map)
        start-time (atom nil)]
    (fn [clock-time]
      (if (nil? @start-time)
        (reset! start-time clock-time))
      (let [time-from-start-time (- clock-time @start-time)
            val (easing-fn start-val end-val duration time-from-start-time)]
        (if (<= time-from-start-time duration)
          (on-update val))
        val))))

(defmethod tween :vector [config-map]
  (println "vector")
  )

(defmethod tween :matrix [config-map]
  (println "matrix")
  )

(defn animate [tween-fn]
  ((fn animation-loop [clock-time]
     (tween-fn clock-time)
     (js/requestAnimationFrame animation-loop)
     )))


(def t (tween {:from      1 :to 1000
               :duration  1000
               :easing-fn ease-out
               :on-update (fn [val]
                            (let [s (dom/by-id "category-Sandwiches")]
                              (layout/position s val 0))
                            )}))

(def t2 (tween {:from [0 0 0] :to [10 10 10]
                :duration 1000
                :easing-fn ease-out
                :on-update println}))
(defn doit []
  (animate t))


