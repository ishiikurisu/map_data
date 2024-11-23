(ns br.eng.crisjr.maps.merge
  (:require [babashka.fs :as fs]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn read-file [folder filename]
  (let [path (str folder "/" filename)]
    (when (fs/exists? path)
      (-> (slurp path)
          json/parse-string))))

(defn read-dir [folder]
  {:places (read-file folder "places.json")
   :pins (read-file folder "pins.json")})

(defn write-map-data! [folder map-data]
  (let [path (str folder "/map_data.json")]
    (->> (json/generate-string map-data
                               {:pretty true})
         (spit path))))

(defn main []
  (let [folders (->> (fs/list-dir ".")
                     (filter fs/directory?)
                     (map str)
                     (filter #(-> %
                                  (str/starts-with? "./.")
                                  not)))
        pins-and-places (reduce (fn [box folder]
                                  (assoc box folder (read-dir folder)))
                                {}
                                folders)]
    (-> (map #(apply write-map-data! %) pins-and-places)
        doall)))

(main)

