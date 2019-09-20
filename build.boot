#!/usr/bin/env boot

(set-env!
 :source-paths #{"src"}
 :dependencies `[[org.clojure/clojure ~(clojure-version) :scope "provided"]])



(def project-name
  "indentor")

(def main-ns
  (symbol (str project-name ".core")))

(def jar-name
  (str project-name ".jar"))

(deftask build
  "Builds an uberjar"
  []
  (comp
   (aot :namespace #{main-ns})
   (uber)
   (jar :file jar-name :main main-ns)
   (sift :include #{(re-pattern jar-name)})
   (target)))
