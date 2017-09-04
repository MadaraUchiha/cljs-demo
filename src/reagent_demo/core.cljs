(ns reagent-demo.core
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [reagent.core :as r]
            [pushy.core :as pushy]))

;; Router

(def app-state (r/atom {}))
(def history (pushy/pushy secretary/dispatch!
                          (fn [x] (when (secretary/locate-route x) x))))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "/")
  (defroute "/" []
    (swap! app-state assoc :page :home))

  (defroute "/about" []
    (swap! app-state assoc :page :about))

  (hook-browser-navigation!)
  (pushy/start! history))

;; -------------------------
;; Views

(defn Link [{to :to} children]
  [:a {:onClick (fn [e]
                  (.preventDefault e)
                  (.info js/console #js {:history history :to to})
                  (pushy/set-token! history to))}
     children])

(defn home []
  [:div [:h1 "Home Page"]
   [Link {:to "/about"} "about page"]])

(defn about []
  [:div [:h1 "About Page"]
   [Link {:to "/"} "home page"]])

;; -------------------------
;; Initialize app

(defmulti current-page #(@app-state :page))
(defmethod current-page :home []
  [home])
(defmethod current-page :about []
  [about])
(defmethod current-page :default []
  [:div "404 Not Found"])


(defn mount-root []
  (r/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (app-routes)
  (mount-root))