(ns food-truck.client.tweenie)

(enable-console-print!)

(defn tween [config-map]
  (let [start-val (:from config-map)
        end-val (:to config-map)
        current-val (atom start-val)
        step-fn (:step-fn config-map)
        on-update (:on-update config-map)]
    (loop [val start-val]
      (on-update val)
      (if (< val end-val)
        (recur (step-fn val))))))


(defn bar []
  (tween {:from 1 :to 100
          :duration 1000
          :step-fn (fn [val] (inc val))
          :on-update (fn [val]
                       (println val)
                       )}))
