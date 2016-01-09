(ns food-truck.math)

(defn row
  "i th row of a matrix m"
  [m i]
  (nth m i))

(defn column
  "j th row of a matrix m"
  [m j]
  (mapv #(nth % j) m))

(defn dot-product [v1 v2]
  (let [v1-with-index (map-indexed (fn [i element] [i element])
                                   v1)]
    (reduce + (for [[i v1-ith-element] v1-with-index
                    :let [v2-ith-element (nth v2 i)]]
                (* v1-ith-element v2-ith-element)))))

(defn matrix-multiply [a b]
  (for [[i a-row] (map-indexed (fn [i row] [i row]) a)]
    (for [j (-> b first count range)
          :let [b-column (column b j)]]
      (dot-product a-row b-column))))
