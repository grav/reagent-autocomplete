(ns reagent-autocomplete.autocomplete
  (:require [reagent.core]
            [clojure.string]))

(def key-codes
  {:arrow-left  37
   :arrow-up    38
   :arrow-right 39
   :arrow-down  40
   :esc         27
   :enter       13})

(defn autocomplete []
  (let [state (reagent.core/atom nil)]
    (fn [{:keys     [value values on-change style]
          filter-fn :filter
          :or {filter-fn (fn [v]
                           #(clojure.string/starts-with? % v))}}]
      (let [{:keys [focus select input]} @state
            {:keys [arrow-up arrow-down esc]} key-codes
            vals (->> values
                      (filter (filter-fn value)))]
        [:div {:style   (merge {:width    "100%"
                                :position :relative})
               :on-blur #(swap! state dissoc :focus)}
         [:input {:type        :text
                  :ref         #(swap! state assoc :input %)
                  :style       {:width "100%"}
                  :on-focus    #(swap! state assoc :focus :text)
                  :on-key-down #(let [key-code (.-keyCode %)]
                                  (cond
                                    (= (:arrow-down key-codes)
                                       key-code) (do (swap! state assoc :focus :dropdown)
                                                     (set! (.-selectedIndex select) 0)
                                                     (.focus select))
                                    (= (:esc key-codes)
                                       key-code) (.blur (.-target %))))
                  :value       value
                  :on-change   #(on-change (.-value (.-target %)))}]

         [:select {:style       {:position :absolute
                                 :width    "100%"
                                 :display  (when-not (#{:dropdown :text} focus)
                                             :none)}
                   :on-key-down #(let [key-code (.-keyCode %)
                                       idx (.-selectedIndex (.-target %))]
                                   (cond (= (:arrow-up key-codes) key-code) (when (= idx 0)
                                                                              (.focus input))
                                         (= (:esc key-codes) key-code) (.blur (.-target %))
                                         (= (:enter key-codes) key-code) (do
                                                                           (on-change (get (vec vals) (.-selectedIndex (.-target %))))
                                                                           (.blur (.-target %)))))
                   :multiple    true
                   :on-focus    #(swap! state assoc :focus :dropdown)
                   :ref         #(swap! state assoc :select %)}
          (for [v vals]
            [:option {:key      v
                      :on-click #(do
                                   (on-change (.-value (.-target %)))
                                   (.blur select))}
             v])]]))))