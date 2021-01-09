(ns sjs.transpiler
  (:require [sjs.reader :as reader]
            [sjs.types :refer [sjs-inner]]
            [clojure.test :refer [deftest is]]
            [clojure.string :as str]
            [sjs.debug :refer [d dm]])
  (:gen-class))

;; TODO: Let's split expression and statement
;; TODO: Add proper indentation
;;  * We may use a prettifier instead.

(declare transpile-stmt)

(defn transpile-const [[_const name val]]
  (format "const %s = %s;" (sjs-inner name) (sjs-inner val)))

(defn transpile-fun-call [[fun & args]]
  ;; How we can distinguish when to insert semi colon;
  ;; FIXME: transpile arguments if they are list.
  (format "%s(%s)" (sjs-inner fun) (str/join ", " (map sjs-inner args))))

(defn transpile-fun-def [[_fn name args & stmts :as x]]
  (printf "hi fun def")
  (d x)
  (format "function %s(%s) {%s}"
          (sjs-inner name)
          (let [args-list (sjs-inner args)]
            (str/join ", " (map sjs-inner args-list)))
          (let [stmt-strings (map transpile-stmt stmts)]
            (str/join "; " stmt-strings))))

(defn transpile-stmt [stmt]
  (let [command (sjs-inner (first (sjs-inner stmt)))]
    (condp = command
      "const" (transpile-const (sjs-inner stmt))
      "fn" (transpile-fun-def (sjs-inner stmt))
      (transpile-fun-call (sjs-inner stmt)))))  

(defn transpile
  [str]
  (printf "hi transpile")
  (let [sexp (reader/read-str str)]
    (transpile-stmt sexp)))

(deftest repltest
  (is (= 1 1))
  (is (= (transpile "(const a 3)") "const a = 3;"))
  (is (= (transpile "(foo 'a' 3)") "foo('a', 3)"))
  ;; Make parser handle '(a b c)
  ;; Make parser handle operator
  (is (= (transpile "(fn foo (list a b c) (+ (+ a b) c))") "")))