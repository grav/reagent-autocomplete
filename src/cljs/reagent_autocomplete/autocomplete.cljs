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
    (fn [{:keys     [value values on-change style delay]
          format-fn :format
          :or       {delay     1000
                     format-fn identity}}]
      (let [{:keys [focus select input]} @state
            {:keys [arrow-up arrow-down esc]} key-codes]
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
                                                                           (on-change (format-fn (get (vec values) (.-selectedIndex (.-target %)))))
                                                                           (.blur (.-target %)))))
                   :multiple    true
                   :on-focus    #(swap! state assoc :focus :dropdown)
                   :ref         #(swap! state assoc :select %)}
          (for [v values]
            [:option {:key      v
                      :on-click #(do
                                   (on-change (format-fn v))
                                   (.blur select))}
             (format-fn v)])]]))))