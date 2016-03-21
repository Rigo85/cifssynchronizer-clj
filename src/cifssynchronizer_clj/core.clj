;Author Rigoberto Leander Salgado Reyes <rlsalgado2006 @gmail.com>
;
;Copyright 2016 by Rigoberto Leander Salgado Reyes.
;
;This program is licensed to you under the terms of version 3 of the
;GNU Affero General Public License. This program is distributed WITHOUT
;ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
;MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
;AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.

(ns cifssynchronizer-clj.core
  (:use org.httpkit.server)
  (:require [cifssynchronizer-clj.cifs-checker :refer :all])
  (:require [cheshire.core :refer :all])
  (:require [cifssynchronizer-clj.checker :refer [create-checker shared-files]])
  (:gen-class))

(def ^:const LOGIN 0)

(def ^:const PROPERTIES 1)

(defmulti get-file-properties
  ""
  (fn [data] (:tag data)))

(defmethod get-file-properties LOGIN
  [{:keys [channel url user password]}]
  (def exist-files? (promise))
  (future (create-checker :threads 25
            :initial-url url
            :read-from-url* (partial read-from-url :user user :password password :url)
            :is-directory* is-directory
            :to-url* to-url
            :observer #(when-not (zero? (count %2)) (deliver exist-files? true))))
  (def pos (atom -1))
  (get-file-properties {:tag PROPERTIES :channel channel}))

(defmethod get-file-properties PROPERTIES
  [{:keys [channel]}]
  (when @exist-files?
    (when-let [file (get @shared-files (swap! pos inc))]
      (send! channel (->> file get-properties generate-string)))))

(defn async-handler [ring-request]
  ""
  (with-channel ring-request channel
    (if (websocket? channel)
      (on-receive channel #(get-file-properties (assoc (parse-string % true) :channel channel)))
      (send! channel {:status 200
                      :headers {"Content-Type" "text/plain"}
                      :body "You need to do a websocket request!"}))))

(defn -main
  ""
  [& args]
  (run-server async-handler {:port 8080}))

