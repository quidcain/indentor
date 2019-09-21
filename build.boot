#!/usr/bin/env boot

(set-env!
 :source-paths #{"src" "test"}
 :dependencies `[[org.clojure/clojure ~(clojure-version) :scope "provided"]
                 [adzerk/boot-test "1.2.0" :scope "test"]
                 [org.clojure/tools.cli "0.4.2"]])

(require '[adzerk.boot-test :refer [test]])

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
