(ns indentor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :refer [as-file]]
            [clojure.string :refer [split]])
  (:gen-class))

(defn env
  [n]
  (System/getenv n))

(defn exit
  [code]
  (System/exit code))

(defn get-indentor-home
  []
  (or
   (env "INDENTOR_HOME")
   #_(throw (Exception. "INDENTOR_HOME is not configured"))
   (str (System/getProperty "user.home") "/.indentor")))

(defn canonize-path
  [path]
  (-> path as-file .getCanonicalPath))

(defn path->path-and-ext
  [path]
  (split path #"\."))

(defn path->dirs
  [path]
  (split path #"/"))

(def set-opts
  [
   ["-p" "--path PATH" "Path to a directory or a file"
    :default ""] ; PWD
   ["-e" "--ext EXTENSION" "Extension of a file to which indentation will be set"]
   ["-s" "--style STYLE"]
   ["-S" "--size SIZE"
    :default 1]
   ])

(def get-opts
  [["-p" "--path PATH" "Path to a directory or a file"]
   ["-e" "--ext EXTENSION" "Extension of a file to which indentation will be set"]])

(defn do-set
  [args]
  (let [result (parse-opts args set-opts)
        opts (:options result)
        [path ext-from-path] (-> opts :path canonize-path path->path-and-ext)
        ext (or (:ext opts) ext-from-path)
        dirs (path->dirs)]
    (println "path: " path " ext:" ext)))

(defn do-get
  [args]
  (println "do-get"))

(defn parse-and-act
  [args]
  (let [action (first args)
        remaining (rest args)]
    (condp = action
      "set" (do-set remaining)
      "get" (do-get remaining)
      (do
        (println "Unexpected action. Use set or get")
        (exit 1)))))

(defn -main
  [& args]
  #_(println "arg" arg rest)
  (parse-and-act args))
