(ns indentor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :refer [as-file make-parents]]
            [clojure.string :refer [split]]
            [clojure.data.json :as json])
  (:gen-class))

(def rules
  "indentor-rules.json")

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
  (map #(.getPath %)
       (loop [curr (as-file path)
              acc (list curr)]
         (let [parent (.getParentFile curr)]
           (if-not parent
             acc
             (recur parent (cons parent acc)))))))

(defn pick-from-indentor-home
  [indentor-home dirs]
  (if (= (first dirs) indentor-home)
    dirs
    (recur indentor-home (next dirs))))

(defn read-data
  [str]
  (json/read-str str :key-fn keyword))

(defn write-data
  [data]
  (json/write-str data))

(defn reduce-rules-files
  [files]
  (->> files
       (map as-file)
       (filter #(.exists %))
       (map #(read-data (slurp %)))
       (reduce merge)))

(def set-opts
  [
   ["-p" "--path PATH" "Path to a directory or a file"
    :default ""] ; PWD
   ["-e" "--ext EXTENSION" "Extension of a file to which indentation will be set"
    :parse-fn keyword]
   ["-s" "--style STYLE"
    :parse-fn keyword]
   ["-S" "--size SIZE"
    :parse-fn #(Integer/parseInt %)
    :default 1]
   ])

(def get-opts
  [["-p" "--path PATH" "Path to a directory or a file"
    :default ""]
   ["-e" "--ext EXTENSION" "Extension of a file to which indentation will be set"
    :parse-fn keyword]])

(defn do-set
  [args]
  (let [opts (:options (parse-opts args set-opts))
        [path ext-from-path] (-> opts :path canjoin-path path->path-and-ext)
        ext (or (:ext opts)
                (keyword ext-from-path)
                (throw (Exception. "Extension is required")))
        style (or (:style opts)
                  (throw (Exception. "Style is required")))
        config-rules-file (as-file (canjoin-path (get-indentor-home) path rules))
        rule {ext {:style style
                   :size (:size opts)}}]
    (spit config-rules-file (write-data (if (.exists config-rules-file)
                                              (-> config-rules-file slurp read-data (merge rule))
                                              (do
                                                (make-parents config-rules-file)
                                                rule))))))

(defn do-get
  [args]
  (let [opts (:options (parse-opts args get-opts))
        [path ext-from-path] (-> opts :path canjoin-path path->path-and-ext)
        ext (or (:ext opts)
                (keyword ext-from-path)
                (throw (Exception. "Extension is required")))
        indentor-home (canjoin-path (get-indentor-home))
        indentor-path (canjoin-path indentor-home path)
        dirs-from-root (path->nesting-dirs indentor-path)
        indentor-dirs (pick-from-indentor-home indentor-home dirs-from-root)
        config-rules-files (map #(canjoin-path % rules) indentor-dirs)]
    (-> config-rules-files reduce-rules-files (get ext) write-data)))

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
    (some-> (parse-and-act args) println)
    (catch Exception e
      (println (.getMessage e))
      (exit 1))))
