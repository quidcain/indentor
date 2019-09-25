(ns indentor.core-test
  (:require [indentor.core :refer :all]
            [clojure.test :refer [deftest is]]
            [clojure.java.io :refer [as-file delete-file]]
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

(defmacro in-test-env
  [form]
  `(let [test-folder# (name (gensym "test"))]
    (.mkdir (as-file test-folder#))
    (with-redefs [env (constantly test-folder#)]
      ~form)
    (delete-file test-folder#)))

(deftest parse-and-act-test
  (in-test-env
   (is (thrown? Exception (parse-and-act (str/split "qwget -e clj -s space" #" "))))))
