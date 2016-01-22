(ns food-truck.client.dom
  (:require [com.kaicode.mercury :as m]
            [cljs.core.async :refer [put! chan]]))

(def window js/window)
(def document js/document)

(.. document (addEventListener "DOMContentLoaded" #(m/broadcast [:dom/ready true])))

(.. window (addEventListener "resize" #(m/broadcast [:window/resize true]) false))

(def whenever-dom-ready (m/whenever :dom/ready :broadcasted))
