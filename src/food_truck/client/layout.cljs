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

(defmethod position str-type [element-id x y]
  (let [transform (-> (translate x y)
                      to-css-matrix)
        element (dom/by-id element-id)]
    (set! (.. element -style -transform) transform)))
