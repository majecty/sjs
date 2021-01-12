(ns sjs.core
  (:gen-class)
  (:require [sjs.reader :as reader]
            [sjs.transpiler :as transpiler]
            [clojure.string :as str]
            [clojure.test :refer [is deftest]]
            [taoensso.timbre :as log]
            [failjure.core :as f]))

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

(defn read-file
  "Read file with error handling"
  [file-name]
  (try
    (slurp file-name)
    (catch Exception e
      (log/trace e)
      (f/fail "Cant't read file %s" file-name))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (f/attempt-all
   [file-name (first args)
    new-file-name (make-output-file-name file-name)
    file-contents (read-file file-name)
    sexps (log/spy :debug "srcs %s"
                    (reader/read-file file-contents))
    jssrcs (map transpiler/transpile sexps)
    jssrc (str (str/join "\n\n" jssrcs) "\n")]

   (spit new-file-name jssrc)

   (f/when-failed
    [e]
    (log/error (f/message e)))))
