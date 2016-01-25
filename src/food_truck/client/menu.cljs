(ns food-truck.client.menu
  (:require-macros [reagent.ratom :as ratom])
  (:require [food-truck.client.dom :as dom]
            [com.kaicode.wocket.client :as ws :refer [process-msg]]
            [com.kaicode.morpheus.transform :as transform]
            [food-truck.client.layout :as layout]
            [reagent.core :as r]
            ))

(enable-console-print!)

(defonce catalog (r/atom nil))
(def active-category (r/atom "Sandwiches"))
(def x (r/atom 10))
(def y (r/atom 10))
(def line-items (r/atom []))

(defonce style (ratom/reaction {:transform (layout/matrix->str (transform/translate @x @y))}))

(defn category-buttons []
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
    (r/create-class  
     {:key (str cat)
      :component-did-mount #(layout/off-screen cat-name)
      :component-will-mount #(println "component-will-mount") 
      :display-name  cat-name
      :reagent-render (fn [category]
                        (let [id cat-name
                              add-cart (fn [product]
                                         (println "add " product)
                                         (swap! line-items conj {:quantity 1 :product product :price 5})
                                         )]
                          [:div {:id    id
                                 :key id
                                 :class "category"
                                 :style {:backgroundColor (str "rgb(0,127,127)")
                                         :border-style "solid"
                                         :border-color "white"
                                         :position "absolute"}}
                           (for [p (:products category)]
                             [:button {:id (:product/sku p)
                                       :key (:product/sku p)
                                       :class "product"
                                       :on-click #(add-cart p)}
                              [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                     :class "product-img"}]
                              [:div  (:product/name p)]])])
                        
                        )})))

(defn cart []
  (let [max-width (- js/window.innerWidth 300)]
    [:table {:id "cart"
             :style {:transform (layout/matrix->str (transform/translate max-width 20))}}
     [:tr [:td "Quantity"] [:td "Description"] [:td "Price"]]
     (for [line @line-items
           :let [product (:product line)]]
       [:tr [:td (:quantity line)] [:td (:product/name product)] [:td (:product/price product)]])
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

(defn my-component
  [x y z]
  (r/create-class  ;; <-- expects a map of functions
   {:component-did-mount ;; the name of a lifecycle function
    #(println "component-did-mount") ;; your implementation
    
    :component-will-mount ;; the name of a lifecycle function
    #(println "component-will-mount") ;; your implementation
    
    ;; other lifecycle funcs can go in here
    
    :display-name  "my-component" ;; for more helpful warnings & errors
    
    :reagent-render ;; Note:  is not :render
    (fn [x y z]     ;; remember to repeat parameters
      [:div {:key (rand)} (str x " " y " " z)])})
  )


;; (r/render [:div
;;            (for [i (range 2)]
;;              ^{:key i} [my-component 1 2 3])
;;            ] js/document.body)
