(ns sjs.types
  (:require [clojure.test :refer [is]])
  (:gen-class))

(defn test-destructure [[type value & tail :as all]]
  (printf "type %s \n value %s \n tail %s \n all %s \n\n" type value tail all))

(test-destructure [])
(test-destructure [1])
(test-destructure [1 2])
(test-destructure [1 2 3])

(defn sjs-type? [[type value & tail]]
  (cond
    (nil? type) false
    (nil? value) false
    (not (nil? tail)) false
    :else true))

(defn sjs-inner
  "The caller of sjs-inner must know the type before use the inner value"
  [[_type value :as all]]
  {:pre (sjs-type? all)}
  value)

(defn sjs-map [[type val :as sjs-val] mapper]
  {:pre (sjs-type? sjs-val)}
  [type (mapper val)])

(defn make-sjs-num [num]
  {:pre (number? num)}
  [:number num])

(defn sjs-num? [[type val :as all]]
  (cond
    (not (sjs-type? all)) false
    (not= type :number) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (number? val)) false
    :else true))

(defn make-sjs-num-biop [biop]
  (fn [a b]
    {:pre [(is (sjs-num? a))
           (is (sjs-num? b))]}
    (let [va (sjs-inner a)
          vb (sjs-inner b)]
      (make-sjs-num (biop va vb)))))

(is (sjs-num? [:number 3]))
(is (not (sjs-num? [:number "3"])))

(defn make-sjs-symbol [str]
  {:pre [(string? str)]}
  [:symbol str])

(defn sjs-symbol? [[type val :as all]]
  (cond
    (not (sjs-type? all)) false
    (not= type :symbol) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (string? val)) false
    :else true))

(defn sjs-symbol->str [[_type value :as symbol]]
  {:pre [(sjs-symbol? symbol)]}
  value)

(defn make-sjs-list [val]
  {:pre [(or (vector? val)
             (seq? val))]}
  [:list val])

(defn sjs-list? [[type val :as all]]
  (cond
    (not (sjs-type? all)) false
    (not= type :list) false
    ;; Should we throw error?
    ;; It is a programmer mistake.
    (not (or (vector? val)
             (seq? val)))
    false

    :else true))

(defn sjs-list-map [mapper sjs-list]
  {:pre [(is (sjs-list? sjs-list))]}
  (sjs-map sjs-list #(map mapper %)))

(defn sjs-list-f-args [sjs-list]
  {:pre (sjs-list? sjs-list)}
  (let [[_type inner-arr] sjs-list
        [f & args] inner-arr]
    [f args]))


