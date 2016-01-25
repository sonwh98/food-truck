(ns food-truck.client.layout
  (:require [com.kaicode.morpheus.transform :as transform]
            [com.kaicode.tweenie :as tweenie]
            [com.kaicode.mercury :as m]
            [domina :as dom]
            [reagent.core :as r]
            [clojure.string :as clj-str]))

(defonce div-type (type (js/document.createElement "div")))

(defmulti position #(type %))
(defmulti off-screen #(type %))
(defmulti on-screen #(type %))
(defmulti rotate #(type %))

(defn matrix->str [m]
  (str "matrix(" (clojure.string/join "," (interleave (first m) (second m))) ")"))

(defn str->matrix [matrix-str]
  (let [vector-of-number (mapv #(js/Number. %)
                               (-> matrix-str
                                   (clj-str/replace #"matrix" "")
                                   (clj-str/replace #"[\(\)]" "")
                                   (clj-str/split ",")))
        two-d-transform [0 0 1]
        matrix (vec (partition 3 vector-of-number))]
    (conj matrix two-d-transform)))

(defn set-transform-matrix! [div matrix]
  (set! (.. div -style -transform)
        (matrix->str matrix)))

(defmethod position div-type [div x y]
  (let [translate-matrix (transform/translate x y)]
    (set-transform-matrix! div translate-matrix)))

(defmethod position js/String [id x y]
  (position (dom/by-id id) x y))

(defmethod off-screen div-type [div]
  (let [width (. div -offsetWidth)
        m  (str->matrix (.. div -style -transform))
        y (-> m second last)
        t (tweenie/tween {:from      0 :to width
                          :duration  1000
                          :easing-fn tweenie/ease-out
                          :on-update (fn [val]
                                       (position div (* -1 val) 20))})]
    (tweenie/animate t)))

(defmethod off-screen js/String [id]
  (let [div (dom/by-id id)]
    (off-screen div)))

(defmethod on-screen div-type [div]
  (let [width (. div -offsetWidth)
        t (tweenie/tween {:from      (* -1 width) :to 0
                          :duration  1000
                          :easing-fn tweenie/ease-out
                          :on-update (fn [val]
                                       (position div val 20))})]
    (tweenie/animate t)))

(defmethod on-screen js/String [id]
  (let [div (dom/by-id id)]
    (on-screen div)))

(defmethod rotate div-type [div theta]
  (set-transform-matrix! div (transform/rotate theta)))

(defmethod rotate js/String [id theta]
  (rotate  (dom/by-id id) theta))

(def widgets (r/atom #{}))

(defn add [div-id]
  (swap! widgets conj div-id))

(m/on :window/resize (fn [evt]
                       (println "recalculate" @widgets)
                       ))
