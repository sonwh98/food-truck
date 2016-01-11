(ns food-truck.matrix
  (:require [food-truck.util :as util]))

(def zero-vector [0 0 0])
(def origin [zero-vector
             zero-vector
             zero-vector])

(defn row
  "i th row of a matrix m"
  [m i]
  (nth m i))

(defn column
  "j th row of a matrix m"
  [m j]
  (mapv #(nth % j) m))

(defn dot-product [v1 v2]
  (let [v1-with-index (util/with-index v1)]
    (reduce + (for [[i v1-ith-element] v1-with-index
                    :let [v2-ith-element (nth v2 i)]]
                (* v1-ith-element v2-ith-element)))))

(defn multiply [a b]
  (for [[i a-row]  (util/with-index a)]
    (for [j (-> b first count range)
          :let [b-column (column b j)]]
      (dot-product a-row b-column))))

(defn add [a b]
  (for [[i a-row] (util/with-index a)
        :let [b-row (nth b i)]]
    (for [[j a-val] (util/with-index a-row)
          :let [b-val (nth b-row j)]]
      (+  a-val b-val))))
