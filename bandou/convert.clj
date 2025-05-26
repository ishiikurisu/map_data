(ns map-data.bandou.convert
  (:require [cheshire.core :as json]
            [clojure.string :as string]
            [clojure.data.xml :as xml]))

(def inlet-file "wikipedia.kml")
(def outlet-file "map_data.geojson")

(defn load [filename]
  (-> filename slurp xml/parse-str))

(defn get-by-tag [root tag]
  (if (xml/element? root)
    (recur [root] tag)
    (->> (filter #(= (:tag %) tag) root)
         (mapcat :content))))

(defn get-in-by-tag [root tags]
  (reduce get-by-tag root tags))

(defn coordinates-from-string [inlet]
  (let [parts (string/split inlet #",")]
    (->> [(first parts) (second parts)]
         (map Float/parseFloat))))

(defn placemark-to-feature [name point i]
  {"type" "Feature"
   "geometry" {"type" "Point"
               "coordinates" point}
   "properties" {"center" false
                 "popup" (str i ". " name)
                 "pin" "not visited"}})

(defn convert-placemarks-to-features [placemarks]
  (let [names (get-by-tag placemarks :name)
        points (->> (get-by-tag placemarks :Point)
                    (mapcat :content)
                    (map coordinates-from-string))]
    (map placemark-to-feature names points (range 1 34))))

(defn convert [inlet]
  (let [placemarks (get-in-by-tag inlet [:kml :Document :Placemark])
        features (convert-placemarks-to-features placemarks)
        center-point {"type" "Feature"
                      "geometry" {"type" "Point"
                                  "coordinates" [35.814 139.964]}
                      "properties" {"center" true
                                    "zoom" 9}}]
    {"type" "FeatureCollection"
     "features" (conj features center-point)
     "metadata" {"pins" {"not visited" {"icon" "calendar"
                                        "markerColor" "red"
                                        "iconColor" "white"
                                        "prefix" "fa"}}}}))

(defn main []
  (let [inlet (load inlet-file)
        data (convert inlet)
        outlet (json/generate-string data {:pretty true})]
    (spit outlet-file outlet)))

; bb convert.clj
(main)

