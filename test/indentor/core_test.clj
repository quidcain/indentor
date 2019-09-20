(ns indentor.core-test
  (:require [indentor.core :refer :all]
            [clojure.test :refer [deftest is]]))

(deftest get-indentor-home-test
  (with-redefs [env (constantly "/home")]
    (is (= (get-indentor-home) "/home") "test when env var exists"))
  (with-redefs [env (constantly nil)]
    (is (= (get-indentor-home) "~/.indentor") "test when env var doesn't exist")))
