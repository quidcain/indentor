(ns indentor.core-test
  (:require [indentor.core :refer :all]
            [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(deftest get-indentor-home-test
  (with-redefs [env (constantly "/home")]
    (is (= (get-indentor-home) "/home") "test when env var exists"))
  (with-redefs [env (constantly nil)]
    (is (= (get-indentor-home)
           (str (System/getProperty "user.home") "/.indentor"))
        "test when env var doesn't exist")))

(deftest path->path-and-ext-test
  (is (= (path->path-and-ext "/dir/file.ext") ["/dir/file" "ext"]))
  (is (= (path->path-and-ext "/dir1/dir2/file.ext") ["/dir1/dir2/file" "ext"]))
  (is (= (path->path-and-ext "/dir1/dir2") ["/dir1/dir2"])))

(deftest path->nesting-dirs-test
  (is (= (path->nesting-dirs "/dir/")
         (list "/" "/dir")))
  (is (= (path->nesting-dirs "/dir1/dir2")
         (list "/" "/dir1" "/dir1/dir2")))
  (is (= (path->nesting-dirs "/")
         (list "/"))))


(defn delete-files-recursively
  [f1]
  (when (.isDirectory (io/file f1))
    (doseq [f2 (.listFiles (io/file f1))]
      (delete-files-recursively f2)))
  (io/delete-file f1))

(defmacro in-test-env
  [& forms]
  `(let [test-folder# (name (gensym "test"))]
    (.mkdir (io/as-file test-folder#))
    (with-redefs [env (constantly test-folder#)]
      ~@forms)
    (delete-files-recursively test-folder#)))

(defn args
  [s]
  (str/split s #" "))

(deftest parse-and-act-test
  (is (thrown? Exception (parse-and-act (args "qwget -e clj -s space"))))
  (is (thrown? Exception (parse-and-act (args "set -p ./file"))))
  (is (thrown? Exception (parse-and-act (args "set -p ./file -e js"))))
  (in-test-env (is (= (do (parse-and-act (args "set -e clj -s tab"))
                          (parse-and-act (args "get -e clj")))
                      {:ext :clj :style :tab :size 1})))
  (in-test-env (is (= (do (parse-and-act (args "set -p ./file.clj -s space -S 4"))
                          (parse-and-act (args "get -p ./file.clj")))
                      {:ext :clj :style :space :size 4}))
               (is (= (parse-and-act (args "get -p ./file -e clj"))
                      {:ext :clj :style :space :size 4})))
  (in-test-env (is (= (do (parse-and-act (args "set -p ./file.clj -s space"))
                          (parse-and-act (args "get -p ./file.clj")))
                      {:ext :clj :style :space :size 1})))
  (in-test-env (is (= (do (parse-and-act (args "set -p ./folder -e clj -s tab"))
                          (parse-and-act (args "set -p ./folder/file.clj -s space -S 2"))
                          (parse-and-act (args "get -p ./folder/file.clj")))
                      {:ext :clj :style :space :size 2})))
  (in-test-env (is (= (do (parse-and-act (args "set -e clj -s space"))
                          (parse-and-act (args "set -e js -s space"))
                          (parse-and-act (args "get -e clj")))
                      {:ext :js :style :space :size 1}) "TODO: Fix different ext overwriting each other")))
