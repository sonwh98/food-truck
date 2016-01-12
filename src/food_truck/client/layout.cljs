(ns food-truck.client.layout
  (:require [food-truck.matrix :as matrix]
            [food-truck.client.dom :as dom]
            [cljsjs.tween]))

(defonce str-type (type "a"))
(defonce div-type (type (js/document.createElement "div")))

(defmulti position #(type %))
(defmulti off-screen #(type %))
(defmulti on-screen #(type %))

(defn translate-x [x]
  [[1 0 x]
   [0 1 0]
   [0 0 1]])

(defn translate-y [y]
  [[1 0 0]
   [0 1 y]
   [0 0 1]])

(defn translate [x y]
  (matrix/multiply (translate-x x)
                   (translate-y y)))

(defn tween [property _ new-val]
  (let [duration 500]
    (.. (js/TWEEN.Tween. property)
        (to (clj->js new-val)
            (+ (* (rand) duration)
               duration))
        (onUpdate (fn []
                    (this-as this
                             (println "this=" this))))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start))))

(defn animate [animation-fn]
  ((fn animation-loop [time]
     (animation-fn time)
     (js/requestAnimationFrame animation-loop))))

(animate (fn [time]
           (.. js/TWEEN update)))

(defn to-css-matrix [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))

(defmethod position div-type [div x y]
  (let [transform (-> (translate x y)
                      to-css-matrix)]
    (set! (.. div -style -transform) transform)))

(defmethod position str-type [id x y]
  (position (dom/by-id id) x y))

(defn morph-matrix [div m new-m]
  (let [duration 500
        m-as-js (clj->js m)
        new-m-as-js (clj->js new-m)]
    (.. (js/TWEEN.Tween. m-as-js)
        (to new-m-as-js
            (+ (* (rand) duration)
               duration))
        (onUpdate (fn []
                    (this-as this
                             (println "this=" this))))
        (easing (.. js/TWEEN -Easing -Exponential -InOut))
        (start)))
  )

(defmethod off-screen div-type [div]
  (let [width (. div -offsetWidth)]
    (position div (* -1 width) 0)))

(defmethod off-screen str-type [id]
  (let [div (dom/by-id id)]
    (off-screen div)))

(defmethod on-screen div-type [div]
  (let [width (. div -offsetWidth)]
    (position div 0 0)))

(defmethod on-screen str-type [id]
  (let [div (dom/by-id id)]
    (on-screen div)))
