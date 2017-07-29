(ns reagent-autocomplete.core
  (:require
    [reagent.core :as reagent]
    [reagent-autocomplete.autocomplete :as auto]
    [clojure.string]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
         (reagent/atom {}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [ratom]
  (let [values ["Oleg" "Olga" "Nana" "Eva" "Malene" "Jonas" "Lisbeth" "Mikkel" "Marianne"]]
    [:div {:style {:width 300}}
     [auto/autocomplete {:values    values
                         :value     (or (:value @ratom) "")
                         :filter    (fn [v]
                                      #(clojure.string/starts-with? (clojure.string/lower-case %)
                                                                    (clojure.string/lower-case v)))
                         :on-change (partial swap! ratom assoc :value)}]

     [:h3 "Possible values:"]
     [:ul
      (for [v values]
        [:li {:key v} v])]
     [:h3 "Selected value:"]
     [:div (:value @ratom)]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
