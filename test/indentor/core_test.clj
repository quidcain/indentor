(ns indentor.core-test
  (:require [indentor.core :refer :all]
            [clojure.test :refer [deftest is]]))

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

(deftest path->dirs-test
  (is (= (path->dirs "/dir/") ["dir"]))
  (is (= (path->dirs "dir") ["dir"]))
  (is (= (path->dirs "/dir") ["dir"]))
  (is (= (path->dirs "/dir1/dir2") ["dir1" "dir2"]))
  (is (= (path->dirs "/dir1//dir2/") ["dir1" "dir2"])))
