(ns sjs.core
  (:require [sjs.reader :as reader]
            [sjs.types :refer [sjs-inner]]
            [clojure.test :refer [deftest is]]
            [clojure.string :as str])
  (:gen-class))

;; Read data
;; Write js code

(defn -main
  "I don't do a whole lot ... yet."
  [& _args]
  (println "Hello, World!"))

(defn transpile-const [[_const name val]]
  (format "const %s = %s;" (sjs-inner name) (sjs-inner val)))

(defn transpile-fun-call [[fun & args]]
  ;; How we can distinguish when to insert semi colon;
  (format "%s(%s)" (sjs-inner fun) (str/join ", " (map sjs-inner args))))


(defn debug-print [a description]
  (printf "%s %s \n" description a)
  nil)
;  a)

(defn transpile
  [str]
  (let [sexp (reader/read-str str)
        command (sjs-inner (first (sjs-inner sexp)))]
    (condp = command
      "const" (transpile-const (sjs-inner sexp))
      (transpile-fun-call (sjs-inner sexp)))))

(deftest repltest
  (is (= 1 1))
  (is (= (transpile "(const a 3)") "const a = 3;"))
  (is (= (transpile "(foo 'a' 3)") "foo('a', 3)")))

