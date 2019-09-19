(ns indentor.core
  (:gen-class))

(defn get-indentor-home
  []
  (or
   (System/getenv "INDENTOR_HOME")
   (throw (Exception. "INDENTOR_HOME is not configured"))))

(defn -main
  [& args]
  (println "hello"))
