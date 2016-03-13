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
  (:gen-class)
  (:require [cifssynchronizer-clj.checker :refer (create-checker shared-files shared-files url-queue)])
  (:import [jcifs.smb NtlmPasswordAuthentication SmbFile]))

(defn read-from-url
  [url]
  (.listFiles (SmbFile. url (NtlmPasswordAuthentication. "uci" "rlsalgado" "Jekyllmarzo.2016"))))

(defn is-directory
  [file]
  (.isDirectory file))

(defn as-url
  [d] (.toString d))

(defn observer
  [old new]
  ;  (printf "old=%s new=%s\n" (count old) (count new)))
  )

(defn test1
  []
  (create-checker 25 "smb://10.1.6.1/descargas$/" read-from-url is-directory as-url observer identity))

