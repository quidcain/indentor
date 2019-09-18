#!/usr/bin/env boot

(require '[boot.cli :refer [defclifn]])
(require '[clojure.string :as str])

(defn parse-path
  [path]
  (let [[without-ext ext] (str/split path #"\.")
        config-files (str/split without-ext #"/")]
        [ext config-files]))

(defn get-indentor-home
  []
  (or
   (System/getenv "INDENTOR_HOME")
   (throw (Exception. "INDENTOR_HOME is not configured"))))

(defclifn -main
  [a awesome bool "Whether you want this app to be awesome or not. (Default true)"]
  (println "Named parameters " *opts*)
  (println "List of arguments " *args*) 
  (get-indentor-home))
