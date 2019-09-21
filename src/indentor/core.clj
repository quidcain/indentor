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
   (str (System/getProperty "user.home") "/.indentor")))

(defn -main
  [& args]
  (println "hello"))
