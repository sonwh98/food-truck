(ns food-truck.client.layout
  (:require [food-truck.matrix :as matrix]
            [food-truck.client.dom :as dom]
            [clojure.string]))

(defonce div-type (type (js/document.createElement "div")))

(defmulti position #(type %))
(defmulti off-screen #(type %))
(defmulti on-screen #(type %))
(defmulti rotate #(type %))

(defn to-css-matrix [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))

(defn set-transform-matrix! [div matrix]
  (set! (.. div -style -transform)
        (to-css-matrix matrix)))

(defmethod position div-type [div x y]
  (let [translate-matrix (matrix/translate x y)]
    (set-transform-matrix! div translate-matrix)))

(defmethod position js/String [id x y]
  (position (dom/by-id id) x y))

(defmethod off-screen div-type [div]
  (let [width (. div -offsetWidth)]
    (position div (* -1 width) 0)))

(defmethod off-screen js/String [id]
  (let [div (dom/by-id id)]
    (off-screen div)))

(defmethod on-screen div-type [div]
  (let [width (. div -offsetWidth)]
    (position div 0 20)))

(defmethod on-screen js/String [id]
  (let [div (dom/by-id id)]
    (on-screen div)))

(defmethod rotate div-type [div theta]
  (set-transform-matrix! div (matrix/rotate theta)))

(defmethod rotate js/String [id theta]
  (rotate  (dom/by-id id) theta))
