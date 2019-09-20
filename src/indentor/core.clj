(ns indentor.core
  (:gen-class))

(defn env
  [n]
  (System/getenv n))

(defn get-indentor-home
  []
  (or
   (env "INDENTOR_HOME")
   #_(throw (Exception. "INDENTOR_HOME is not configured"))
   "~/.indentor"))

(defn -main
  [& args]
  (println "hello"))
