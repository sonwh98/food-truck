(ns food-truck.client.menu
  (:require-macros [reagent.ratom :as ratom])
  (:require [food-truck.client.dom :as dom]
            [food-truck.client.ws :as ws :refer [process-msg]]
            [com.kaicode.morpheus.transform :as transform]
            ;;[com.kaicode.morpheus.matrix :as matrix]
            [food-truck.client.layout :as layout]
            [food-truck.client.tweenie]
            [reagent.core :as r]
            ))

(enable-console-print!)

(defonce catalog (r/atom nil))
(def active-category (r/atom "category-Sandwiches"))
(def x (r/atom 10))
(def y (r/atom 10))

(defonce style (ratom/reaction {:transform (layout/matrix->str (transform/translate @x @y))}))

(defn category-buttons []
  [:div {:id "category-buttons-container"
         :style @style}
   (for [category @catalog
         :let [cat-name (:category/name category)]]
     [:button {:id cat-name
               :key cat-name
               :on-click (fn [evt]
                           (let [new-active-category (str "category-" cat-name)]
                             (layout/off-screen @active-category)
                             (layout/on-screen new-active-category)
                             (reset! active-category new-active-category)
                             )
                           
                           )} cat-name])])

(defn category [cat]
  (let [cat-name (:category/name cat)]
    (r/create-class  
     {:key (str cat)
      :component-did-mount #(layout/off-screen (str "category-" cat-name))
      :component-will-mount #(println "component-will-mount") 
      :display-name  cat-name
      :reagent-render (fn [category]
                        (let [id (str "category-" cat-name)]
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
                                       :class "product"}
                              [:img {:src (or (:url p) "http://www.creattor.com/files/10/652/drinks-icons-screenshots-1.png")
                                     :class "product-img"}]
                              [:div  (:product/name p)]])])
                        
                        )})))


(defn app []
  [:div
   [category-buttons]
   (for [cat @catalog]
     ^{:key (:category/name cat)} [category cat]
     )])

(defn build-ui []
  (r/render [app] js/document.body)
  (layout/on-screen "category-Sandwiches"))


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
