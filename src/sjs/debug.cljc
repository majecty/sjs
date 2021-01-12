(ns sjs.debug)

(defn d [arg]
  (printf "debug: %s" arg)
  arg)

(defn dm [arg message]
  (printf "%s %s\n" message arg)
  arg)
