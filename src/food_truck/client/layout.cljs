(ns food-truck.client.layout
  (:require [food-truck.matrix :as matrix]))

(defonce str-type (type "a"))
(defonce div-type (type (js/document.createElement "div")))

(defmulti position #(type %))
(defmethod position str-type [element-id x y z]
  element-id)

(defn translate-x [x]
  [[1 0 x]
   [0 1 0]
   [0 0 1]])

(defn to-css-matrix [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))
