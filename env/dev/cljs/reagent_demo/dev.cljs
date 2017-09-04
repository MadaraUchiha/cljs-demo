(ns ^:figwheel-no-load reagent-demo.dev
  (:require
    [reagent-demo.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
