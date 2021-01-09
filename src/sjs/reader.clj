(ns sjs.reader
  (:require [blancas.kern.core :as kern]
            [blancas.kern.lexer.basic :as lexer]
            [clojure.test :refer [is deftest]]
            [sjs.types :refer [make-sjs-num make-sjs-symbol make-sjs-list]])
  (:gen-class))

(declare p-s-expr)

(def p-list (->> #'p-s-expr
                 lexer/lexeme
                 kern/many0
                 lexer/parens
                 (kern/<$> make-sjs-list)))

(defn symbol-char? [c]
  (and
   (not (Character/isWhitespace c))
   (not= c \()
   (not= c \))))

(deftest test-symbol-char?
  (is (symbol-char? \a))
  (is (not (symbol-char? \())))

(def p-symbol-char
  (kern/<?> (kern/satisfy symbol-char?)
            "symbol character"))

(deftest test-p-symbol-char
  (is (= \a (kern/value p-symbol-char "as")))
  (is (not (kern/value p-symbol-char "("))))

(def p-symbol
  (->> p-symbol-char
       (kern/many1)
       (kern/<+>)
       (kern/<$> make-sjs-symbol)))

(deftest test-p-symbol
  (is (= (make-sjs-symbol "as") (kern/value p-symbol "as"))))

(def p-number
  (kern/<$> make-sjs-num lexer/dec-lit))

(def p-s-expr (kern/<|> p-list p-number p-symbol))

(deftest test-p-s-expr
  (is (= (make-sjs-list [(make-sjs-num 1) (make-sjs-symbol "as")]) (kern/value p-s-expr "(1 as)"))))

(defn read-str [str]
  (kern/value p-s-expr str))

(deftest test-read-str
  (is (= [(make-sjs-list [(make-sjs-num 1) (make-sjs-symbol "as")])] (read-str "(1 as)"))))
