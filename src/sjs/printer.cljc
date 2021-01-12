(ns sjs.printer
  (:require [clojure.test :refer [is]]
            [clojure.string :as str])
  (:gen-class))

(defn sjs-pr-str [[type value]]
  (cond
    (= type :number) (str value)
    (= type :symbol) value
    (= type :list)
    (str "("
         (str/join " " (map sjs-pr-str value))
         ")")
    :else nil))

(is (= "3" (sjs-pr-str [:number 3])))
(is (= "a" (sjs-pr-str [:symbol "a"])))
(is (= "(3 as)" (sjs-pr-str [:list [[:number 3] [:symbol "as"]]])))
(is (= "(3 as (1 2))" (sjs-pr-str [:list [[:number 3]
                                          [:symbol "as"]
                                          [:list
                                           [[:number 1]
                                            [:number 2]]]]])))
(is (nil? (sjs-pr-str nil)))

