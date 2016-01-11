(ns food-truck.client.layout
  (:require [food-truck.matrix :as matrix]
            [food-truck.client.dom :as dom]))

(defonce str-type (type "a"))
(defonce div-type (type (js/document.createElement "div")))

(defmulti position #(type %))

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

(defn to-css-matrix [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))

(defmethod position div-type [div x y]
  (let [transform (-> (translate x y)
                      to-css-matrix)]
    (set! (.. div -style -transform) transform)))

(defmethod position str-type [id x y]
  (position (dom/by-id id) x y))

