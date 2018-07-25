(ns clj-vchats.components.pixi-texture-mesh
  (:require [reagent.core :as r]
            [cljsjs.pixi]))

(defn init [])
(def renderer
  (js/PIXI.WebGLRenderer.
   500
   400
   (clj->js {:backgroundColor "0x1099bb"})))

(defn view []
  (.appendChild
   (.-body js/document)
   (.-view renderer)))

(def stage
  (js/PIXI.Container.))

