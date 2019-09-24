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

(defn canjoin-path
  "Joins and canonizes path"
  [p & ps]
  (str (.normalize (.toAbsolutePath (java.nio.file.Paths/get p (into-array String ps))))))

(defn get-indentor-home
  []
  (or
   (env "INDENTOR_HOME")
   #_(throw (Exception. "INDENTOR_HOME is not configured"))
   (canjoin-path (System/getProperty "user.home") "/.indentor")))

(defn path->path-and-ext
  [path]
  (split path #"\."))

(defn path->nesting-dirs
  [path]
  (loop [curr (as-file path)
         acc (list curr)]
    (let [parent (.getParentFile curr)]
      (if-not parent
        acc
        (recur parent (cons parent acc))))))

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
        [path ext-from-path] (-> opts :path canjoin-path path->path-and-ext)
        ext (or (:ext opts) ext-from-path)
        dirs (path->dirs path)]
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
      (throw (Exception. "Unexpected action. Use set or get")))))

(defn -main
  [& args]
  (try
    (parse-and-act args)
    (catch Exception e
      (println (.getMessage e))
      (exit 1))))
