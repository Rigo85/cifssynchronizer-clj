;Author Rigoberto Leander Salgado Reyes <rlsalgado2006 @gmail.com>
;
;Copyright 2016 by Rigoberto Leander Salgado Reyes.
;
;This program is licensed to you under the terms of version 3 of the
;GNU Affero General Public License. This program is distributed WITHOUT
;ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
;MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
;AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.

(ns cifssynchronizer-clj.cifs-checker
  (:import [jcifs.smb NtlmPasswordAuthentication SmbFile])
  (:require [clojure.string :refer [split]]))

(defn read-from-url
  "Read files using cifs"
  [& {:keys [url user password] :or {url "" user "" password ""}}]
  (->> user
    (#(split % #"\\"))
    (#(if (second %) % (cons "" %)))
    vec
    (#(conj % password))
    (#(NtlmPasswordAuthentication. (get % 0) (get % 1) (get % 2)))
    (SmbFile. url)
    .listFiles))

(defn is-directory
  ""
  [file]
  (.isDirectory file))

(defn to-url
  ""
  [d]
  (.toString d))

(defn get-properties
  ""
  [file]
  {:name (.getName file)
   :path (.getCanonicalPath file)
   :size (.length file)
   :time (.getDate file)})
