(ns food-truck.client.menu
  (:require-macros [reagent.ratom :as ratom])
  (:require [food-truck.client.dom :as dom]
            [com.kaicode.wocket.client :as ws :refer [process-msg]]
            [com.kaicode.morpheus.transform :as transform]
            [com.kaicode.tily :as util]
            [food-truck.client.layout :as layout]
            [reagent.core :as r]))

(enable-console-print!)

(defonce catalog (r/atom nil))
(def active-category (r/atom "Sandwiches"))
(def x (r/atom 10))
(def y (r/atom 10))
(def line-items (r/atom []))

(defonce style (ratom/reaction {:transform (layout/matrix->str (transform/translate @x @y))}))

(defn category-buttons []
  (layout/add "category-buttons-container")
  [:div {:id "category-buttons-container"
         :style @style}
   (for [category @catalog
         :let [cat-name (:category/name category)]]
     [:button {:id (str "button-" cat-name)
               :key cat-name
               :on-click (fn [evt]
                           (let [new-active-category cat-name]
                             (layout/off-screen @active-category)
                             (layout/on-screen new-active-category)
                             (reset! active-category new-active-category)))} cat-name])])


(defn category [cat]
  (let [cat-name (:category/name cat)]
    (layout/add cat-name)
    (r/create-class  
     {:key (str cat)
      :component-did-mount #(layout/off-screen cat-name)
      :component-will-mount #(println "component-will-mount") 
      :display-name  cat-name
      :reagent-render (fn [category]
                        (let [id cat-name
                              add-to-cart (fn [product]
                                            (let [line-items-with-index (util/with-index @line-items)
                                                  line-item-with-index (first (filter #(= (-> % second :product) product)
                                                                               line-items-with-index))
                                                  [index line-item] line-item-with-index]
                                              (if (empty? line-item)
                                                (swap! line-items conj {:quantity 1 :product product :price (:product/price product)})
                                                (do
                                                  (swap! line-items update-in [index :quantity] inc)
                                                  (swap! line-items update-in [index :price] (fn [old-price]
                                                                                               (+ old-price (:product/price product))))))))]
                          [:div {:id    id
                                 :key id
                                 :class "category"
                                 :style {:backgroundColor (str "rgb(0,127,127)")
                                         :border-style "solid"
                                         :border-color "white"
                                         :position "absolute"}}
                           (for [product (:products category)]
                             [:button {:id (:product/sku product)
                                       :key (:product/sku product)
                                       :class "product"
                                       :on-click #(add-to-cart product)}
                              [:img {:src (or (:url product) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                     :class "product-img"}]
                              [:div  (:product/name product)]])]))})))

(defn cart []
  (let [max-width (- js/window.innerWidth 800)]
    [:table {:id "cart"
             :style {:transform (layout/matrix->str (transform/translate max-width 20))}}
     [:tr [:td "Quantity"] [:td "Description"] [:td "Price"]]
     (for [line @line-items
           :let [product (:product line)]]
       ^{:key (str line)} [:tr [:td (:quantity line)] [:td (:product/name product)] [:td (:price line)]])
     ]))

(defn app []
  [:div
   [category-buttons]
   (for [cat @catalog]
     ^{:key (:category/name cat)} [category cat]
     )
   [cart]]
  )

(defn build-ui []
  (r/render [app] js/document.body)
  (layout/on-screen "Sandwiches"))


(defmethod process-msg :catalog [[_ catalog-from-server]]
  (reset! catalog catalog-from-server)
  (build-ui))

(defn send-get-catalog []
  (dom/whenever-dom-ready #(ws/send! [:get-catalog true])))

(defn on-js-reload []
  (println "reload")
  (send-get-catalog))
