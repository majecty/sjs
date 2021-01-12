(ns sjs.core
  (:gen-class)
  (:require [sjs.reader :as reader]
            [sjs.transpiler :as transpiler]
            [clojure.string :as str]
            [clojure.test :refer [is deftest]]))

(defn split-ext [file-name]
  (let [[_whole wo-ext ext] (re-matches #"(.+)\.([a-zA-z0-9]+)$" file-name)]
    (if (and (some? wo-ext) (some? ext))
      (list wo-ext ext)
      file-name)))

(deftest split-ext-test
  (is (= (split-ext "a/b.c") "a/b c"))
  (is (= (split-ext "a/bc") "a/bc")))

(defn make-output-file-name [file-name]
  (let [[base _exp] (split-ext file-name)
        new-name (str base ".js")]
    new-name))

;; FIXME: handle file not found
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [file-name (first args)
        new-file-name (make-output-file-name file-name)
        file-contents (slurp file-name)
        sexps (let [sexps (reader/read-file file-contents)]
                (printf "s-expressions %s\n" sexps)
                sexps)
        jssrcs (let [srcs (map transpiler/transpile sexps)]
                 (printf "srcs %s\n" srcs)
                 srcs)
        jssrc (str (str/join "\n\n" jssrcs) "\n")]
    (spit new-file-name jssrc)))
