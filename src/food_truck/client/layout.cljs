(ns food-truck.client.layout
  (:require [food-truck.matrix :as matrix]
            [food-truck.client.dom :as dom]
            [clojure.string]))

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

(defn scale-x [factor]
  [[factor 0 0]
   [0 1 0]
   [0 0 1]])

(defn scale-y [factor]
  [[1 0 0]
   [0 factor 0]
   [0 0 1]])

(defn scale [factor]
  (matrix/multiply (scale-x factor)
                   (scale-y factor)))

(defn to-css-matrix [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))

(defn set-transform-matrix! [div matrix]
  (set! (.. div -style -transform)
        (to-css-matrix matrix)))

(defmethod position div-type [div x y]
  (let [translate-matrix (translate x y)]
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
