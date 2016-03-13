;Author Rigoberto Leander Salgado Reyes <rlsalgado2006 @gmail.com>
;
;Copyright 2016 by Rigoberto Leander Salgado Reyes.
;
;This program is licensed to you under the terms of version 3 of the
;GNU Affero General Public License. This program is distributed WITHOUT
;ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
;MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
;AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.

(ns cifssynchronizer-clj.checker
  (:import [java.util.concurrent LinkedBlockingQueue BlockingQueue]))

(def url-queue
  "Queue to store URLs folders to process."
  (LinkedBlockingQueue.))

(def shared-files
  "Shared files."
  (atom []))

; Forward declarations.
(def read-from-url)
(def is-directory)
(def to-url)
(def file-filter)
(def agents)
(declare handle-results run)

(defn ^::blocking get-url
  "Take one URL from 'url-queue' and get the content."
  [{:keys [^BlockingQueue queue] :as state}]
  (try
    {:content (->> queue .take read-from-url)
     ::t #'handle-results}
    (catch Exception e
      ;; skip any URL we failed to load
      state)
    (finally (run *agent*))))

(defn ^::blocking handle-results
  "Split some URL content in 'directories' and 'files',
  put 'directories' in  'url-queue' and 'files' in 'shared-files'"
  [{:keys [content]}]
  (try
    (let [{:keys [directories files]} (group-by #(if (is-directory %) :directories :files) content)]
      (doseq [d directories]
        (.put url-queue (to-url d)))
      (swap! shared-files (partial apply conj) (filterv file-filter files))
      {::t #'get-url :queue url-queue})
    (finally (run *agent*))))

(defn run
  "Run agent(s)"
  ([] (doseq [a agents] (run a)))
  ([a]
    (when (agents a)
      (send a (fn [{transition ::t :as state}]
                (let [dispatch-fn (if (-> transition meta ::blocking)
                                    send-off
                                    send)]
                  (dispatch-fn *agent* transition))
                state)))))

(defn create-checker
  "Create and activate agents to get every files from 'initial-url'
  threads => agents count.
  initial-url => root URL.
  read-from-url* => how to read from some URL (depend on comunication protocol).
  is-directory* => directory or not.
  to-url* => convert to URL.
  observer => what to do with the updates.
  file-filter* => which files to keep.
  "
  [threads initial-url read-from-url* is-directory* to-url* observer file-filter*]
  (def read-from-url read-from-url*)
  (def is-directory is-directory*)
  (def to-url to-url*)
  (def file-filter file-filter*)
  (add-watch shared-files :observer (fn [key identity old new] (observer old new)))
  (reset! shared-files [])
  (.clear url-queue)
  (.put url-queue initial-url)
  (def agents (set (repeatedly threads #(agent {::t #'get-url :queue url-queue}))))
  (run))


