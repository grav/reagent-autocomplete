(ns reagent-autocomplete.core
  (:require
    [reagent.core :as reagent]
    [reagent-autocomplete.autocomplete :as auto]
    [clojure.string]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
         (reagent/atom {}))

(defn search [q]
  (js/console.log "Search '" q "'")
  (js/Promise.resolve (->> ["Oleg" "Olga" "Nana" "Eva" "Malene" "Malene" "Jonas" "Marianne" "Lisbeth" "Mikkel" "Marianne"]
                           (filter #(clojure.string/starts-with? (clojure.string/lower-case %)
                                                                 (clojure.string/lower-case q)))
                           (map-indexed vector))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page []
  (let [delay 1000]
    (fn [ratom]
      (let [{:keys [values value]
             :or   []} @ratom]
        [:div {:style {:width 300}}
         [auto/autocomplete {:values    values
                             :value     value
                             :format    last
                             :on-change #(do (swap! ratom assoc :value %)

                                             (js/setTimeout (fn []
                                                              (when (= (:value @ratom) %)
                                                                (.then (search %)
                                                                       (fn [es]
                                                                         (swap! ratom assoc :values (->> es
                                                                                                         (take 10)))))))
                                                            delay))}
          ]
         [:h3 "Possible values:"]
         [:ul
          (for [v values]
            [:li {:key v} (pr-str v)])]
         [:h3 "Selected value:"]
         [:div (pr-str (:value @ratom))]]))))


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
