;; Copyright (c) Alan Dipert and Micha Niskin. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.

(ns demo.http.rules
  (:refer-clojure :exclude [assert])
  (:require
    [castra.core :refer [ex *request* *session*]]))

;;; utility ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def auth ::auth)
(defmacro assert [expr & [msg]]
  `(when-not ~expr (throw (ex (or ~msg "Server error.") {:from ::assert} auth))))

;;; internal ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-pass   [db-val user]  (get-in db-val [:users user :pass]))
(defn available? [db-val user]  (nil? (get-in db-val [:users user])))
(defn do-login!  [user]         (let [new-session (swap! *session* assoc :user user)] (prn :login-session new-session) new-session))
;;; public ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn allow       []      (constantly true))
(defn deny        []      (throw (ex auth "Permission denied.")))
(defn logout!     []      (swap! *session* assoc :user nil))
(defn logged-in?  []
  (add-watch *session* :logged-in
    (fn [k r o n] (prn :old o :new n :key k)))
  (or (get @*session* :user)
      (prn :session @*session*)
      (prn :will-throw)
      (throw (ex "Please log in." {:state nil :status 403}))))
(defn self?       [user]  (assert (= (str user) (str (:user @*session*)))))

(defn register! [db user pass1 pass2]
  (assert (= pass1 pass2) "Passwords don't match.")
  (swap! db #(do (assert (available? % user) "Username not available.")
                   (assoc-in % [:users user] {:pass pass1})))
  (do-login! user))

(defn login! [db user pass]
  (assert (= pass (get-pass @db user)) "Bad username/password.")
  (add-watch *session* :login (fn [k r o n] (prn :old o :new n :key k)))
  (do-login! user))
