(ns food-truck.client.tweenie)

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
      (easing-fn start-val end-val duration (- time @start-time)))))

(defn animate [tween-fn]
  ((fn animation-loop [time]
     (let [request-id (js/requestAnimationFrame animation-loop)
           return-val (tween-fn time)]
       (if (nil? return-val)
         (js/cancelAnimationFrame request-id))))))

(def t (tween {:from 1 :to 10
               :duration 10000
               :easing-fn linear-easing
               :on-update (fn [val]
                            (println val)
                            )}))

(defn doit []
  (animate t))
