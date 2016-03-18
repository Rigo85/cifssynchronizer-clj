;Author Rigoberto Leander Salgado Reyes <rlsalgado2006 @gmail.com>
;
;Copyright 2016 by Rigoberto Leander Salgado Reyes.
;
;This program is licensed to you under the terms of version 3 of the
;GNU Affero General Public License. This program is distributed WITHOUT
;ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
;MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
;AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.

(defproject cifssynchronizer-clj "0.1.0"
  :description "Backend to read files from some shared resource
  and publish those files in a local webserver"
  :url "http://example.com/FIXME"
  :license {:name "GNU Affero General Public License"
            :url "http:www.gnu.org/licenses/agpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [jcifs/jcifs "1.3.17"]
                 [http-kit "2.1.18"]
                 [cheshire "5.5.0"]]
  :main ^:skip-aot cifssynchronizer-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
