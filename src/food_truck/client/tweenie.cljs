(ns food-truck.client.tweenie)

(enable-console-print!)

(defn tween [config-map]
  (let [start-val (:from config-map)
        end-val (:to config-map)
        duration (:duration config-map)
        step-fn (:step-fn config-map)
        on-update (:on-update config-map)
        
        current-val (atom start-val)
        start-time (atom nil)
        tick-time (atom nil)]
    (fn [time]
      (let [now (js/performance.now) ]
        (if (nil? @start-time)
          (reset! start-time now))        
        (reset! tick-time now))

      (if (and (< (- @tick-time @start-time)
                  duration)
               (< @current-val end-val))
        (let [next-val (step-fn @current-val)]
          (println "duration " (- @tick-time @start-time)) 
          (on-update @current-val)
          (reset! current-val next-val))
        nil))))

(defn animate [tween-fn]
  ((fn animation-loop [time]
     (let [request-id (js/requestAnimationFrame animation-loop)
           return-val (tween-fn time)]
       (if (nil? return-val)
         (do
           (println "cancel" request-id)
           (js/cancelAnimationFrame request-id)))))))



(def t (tween {:from 1 :to 100
               :duration 10000
               :step-fn (fn [val]
                          (inc val))
               :on-update (fn [val]
                            (println val)
                            )}))

(defn doit []
  (animate t))
