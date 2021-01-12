(ns sjs.core
  (:gen-class)
  (:require [sjs.reader :as reader]
            [sjs.transpiler :as transpiler]
            [clojure.string :as str]
            [clojure.test :refer [is deftest]]
            [clojure.tools.logging :as log]))

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
        sexps (log/spyf "srcs %s"
                        (reader/read-file file-contents))
        jssrcs (map transpiler/transpile sexps)
        jssrc (str (str/join "\n\n" jssrcs) "\n")]
    (spit new-file-name jssrc)))
