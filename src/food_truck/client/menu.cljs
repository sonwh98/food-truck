(ns food-truck.client.menu
  (:require-macros [reagent.ratom :as ratom])
  (:require [food-truck.client.dom :as dom]
            [food-truck.client.ws :as ws :refer [process-msg]]
            [food-truck.client.layout :as layout]
            [food-truck.client.tweenie]
            [reagent.core :as r]
            ))

(enable-console-print!)

(defonce catalog (r/atom nil))
(def x (r/atom 10))
(def y (r/atom 10))

(defonce style (ratom/reaction {:transform (layout/to-css-matrix (layout/translate @x @y))}))

(defn category-buttons []
  [:div {:id "category-buttons-container"
         :style @style}
   (for [category @catalog
         :let [cat-name (:category/name category)]]
     [:button {:id cat-name
               :key cat-name
               :on-click (fn [evt]
                           (println "cat" cat-name)
                           )} cat-name])])

(defn categories []
  (doall (for [category @catalog
               :let [color (-> (* (rand) 0.5) (+ 0.25))
                     products (:products category)
                     id (str "category-" (:category/name category))]]
           [:div {:id    id
                  :key id
                  :class "category"
                  :style {:backgroundColor (str "rgb(0,127,127)")
                          :border-style "solid"
                          :border-color "white"
                          :position "absolute"
                          :transform (layout/to-css-matrix (layout/translate @x (+ @y 10)))
                          }}
            (for [p products]
              [:button {:id (:product/sku p)
                        :key (:product/sku p)
                        :class "product"}
               [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                      :class "product-img"}]
               [:div  (:product/name p)]])])))

(defn app []
  [:div
   [category-buttons]
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


;;(send-get-catalog)

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
