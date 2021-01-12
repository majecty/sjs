(ns sjs.transpiler
  (:require [sjs.reader :as reader]
            [sjs.types :refer [sjs-inner sjs-list? sjs-num? sjs-symbol?]]
            [clojure.test :refer [deftest is]]
            [clojure.string :as str])
  (:gen-class))

;; TODO: Let's split expression and statement
;; TODO: Add proper indentation
;;  * We may use a prettifier instead.

(declare transpile-stmt)
(declare transpile-expression)

;; TODO: support (+ a b c)
(defn transpile-plus [[_plus a b]]
  (format "(%s + %s)"
          (transpile-expression a)
          (transpile-expression b)))

(defn transpile-expression
  "Transpile expression to string"
  [exp]
  (cond
    (sjs-list? exp)
    (let [command (sjs-inner (first (sjs-inner exp)))]
      (condp = command
        "+" (transpile-plus (sjs-inner exp))))

    (sjs-num? exp)
    (sjs-inner exp)

    (sjs-symbol? exp)
    (sjs-inner exp)

    ;; FIXME: Do proper error handling
    :else (format "?? %s" exp)))

(defn transpile-const [[_const name val]]
  (format "const %s = %s;" (sjs-inner name) (transpile-expression val)))

(defn transpile-fun-call [[fun & args]]
  ;; How we can distinguish when to insert semi colon;
  ;; FIXME: transpile arguments if they are list.
  (format "%s(%s)" (sjs-inner fun) (str/join ", " (map transpile-expression args))))

(defn transpile-fun-def [[_fn name args & stmts]]
  (format "function %s(%s) {%s}"
          (sjs-inner name)
          (let [args-list (sjs-inner args)]
            (str/join ", " (map sjs-inner args-list)))
          (let [stmt-strings (map transpile-stmt stmts)]
            (str/join "; " stmt-strings))))

(defn transpile-return [[_return exp]]
  (format "return %s" (transpile-expression exp)))

(defn transpile-stmt [stmt]
  (let [command (sjs-inner (first (sjs-inner stmt)))]
    (condp = command
      "const" (transpile-const (sjs-inner stmt))
      "fn" (transpile-fun-def (sjs-inner stmt))
      "return" (transpile-return (sjs-inner stmt))
      (transpile-fun-call (sjs-inner stmt)))))

(defn transpile [sexp]
  (transpile-stmt sexp))

(defn transpile-str
  [str]
  (let [sexp (reader/read-str str)]
    (transpile-stmt sexp)))

(deftest repltest
  (is (= 1 1))
  (is (= (transpile-str "(const a 3)") "const a = 3;"))
  (is (= (transpile-str "(const a (+ 3 1))") "const a = (3 + 1);"))
  (is (= (transpile-str "(foo 'a' 3)") "foo('a', 3)"))
  ;; Make parser handle '(a b c)
  ;; Make parser handle operator
  (is (= (transpile-str "(fn foo (list a b c) (return (+ (+ a b) c)))")
         "function foo(list, a, b, c) {return ((a + b) + c)}")))
