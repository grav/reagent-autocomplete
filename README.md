# reagent-autocomplete

A [reagent](https://github.com/reagent-project/reagent) component for
an autocompleting input text field.

Minimal example:

```
[:div {:style {:width 300}}
  [reagent-autocomplete/autocomplete
    {:values ["Oleg" "Olga "Agnethe" "Justus"]
     :value (:value @my-state)
     :on-change #(swap! my-state assoc :value %)}]]
```

The component is controlled, meaning you need to maintain the state,
eg supplying the state (`:value`) and a callback to update the state
(`:on-change`).

You also supply the possible values (`:values`) and, optionally,
a filter fn (`:filter`) that takes the currently entered value
and returns a predicate function:

```
[:div {:style {:width 300}}
  [reagent-autocomplete/autocomplete
    {:values ["Oleg" "Olga "Agnethe" "Justus"]
     :value (:value @my-state)
     :filter (fn [v] #(= % (apply str (reverse v))))
     :on-change #(swap! my-state assoc :value %)}]]
```


## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build

```
lein clean
lein cljsbuild once min
```
