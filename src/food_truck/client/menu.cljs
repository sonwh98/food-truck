(ns food-truck.client.menu
  (:require-macros [reagent.ratom :as ratom])
  (:require [food-truck.client.dom :as dom]
            [food-truck.client.ws :as ws :refer [process-msg]]
            [food-truck.matrix :as math]
            [reagent.core :as r]
            [cljsjs.tween]))

(enable-console-print!)

(defonce catalog (r/atom nil))
(def x (r/atom 200))

(defonce style (ratom/reaction {:transform (math/to-css-matrix (math/translate-x math/origin @x)) }))

(defn category-buttons []
  [:div {:id "category-buttons-container"
         :style @style}
   (for [category @catalog
         :let [cat-name (:category/name category)]]
     [:button {:id cat-name
               :key cat-name} (str "bar4 " cat-name)])])

(defn categories []
  (for [category @catalog
        :let [color (-> (* (rand) 0.5) (+ 0.25))
              products (:products category)
              id (str "category-" (:category/name category))]]
    [:div {:id    id
           :key id
           :class "category"
           :style {:backgroundColor (str "rgb(0,127,127)")
                   :border-style "solid"
                   :border-color "white"
                   }}
     (for [p products]
       [:button {:id (:product/sku p)
                 :key (:product/sku p)
                 :class "product"}
        [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
               :class "product-img"}]
        [:div  (:product/name p)]])]))

(defn app []
  [:div
   (category-buttons)
   (categories)])

(defn build-ui []
  (r/render [app] js/document.body)
  )

(defmethod process-msg :catalog [[_ catalog-from-server]]
  (reset! catalog catalog-from-server)
  (build-ui))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload []
  (println "reload")
  (send-get-catalog))


;(send-get-catalog)


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


;; (def foo (dom/by-id "category-buttons-container"))
;; (def style (. foo -style))

;; (def coords #js{:x 0 :y 0})
;; (def t (.. (js/TWEEN.Tween. coords)
;;                (to #js{:x 100 :y 100} 1000)
;;                (onUpdate (fn []
;;                            (this-as this
;;                                     (println "this=" this))
;;                            ))))

;; (.. t start)

;; (animate (fn [time]
;;            (.. js/TWEEN update)))
