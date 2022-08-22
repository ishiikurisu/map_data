(ns br.eng.crisjr.util.map.build
  (:require [cheshire.core :as json]))

(def places-md-file-name "raw_places.table.md")
(def pins-md-file-name "raw_pins.table.md")
(def places-json-file-name "places.json")
(def pins-json-file-name "pins.json")

(defn- line-to-array [line]
  (->> (clojure.string/split line #"\|")
       (filter #(not= % ""))
       (map clojure.string/trim)))

(defn load-mdts [filename]
  (let [lines (clojure.string/split (slurp filename) #"\n")
        fields (-> lines first line-to-array)]
    (->> lines
         (drop 2)
         (map line-to-array)
         (map (fn [entry]
                (reduce (fn [state [index item]]
                          (assoc state
                                 (nth fields index)
                                 item))
                        {}
                        (map-indexed (fn [index item]
                                       [index item])
                                     entry)))))))

(defn convert-places [inlet pins outlet]
  (->> inlet
       (load-mdts)
       (map (fn [p]
              {"lat" (get p "lat")
               "lon" (get p "lon")
               "popup" (get p "poke")
               "status" (get (->> pins
                                  (filter #(= (get p "gen") 
                                              (get % "gen")))
                                  first)
                             "region"
                             "default")}))
       json/generate-string
       (spit outlet)))

(defn convert-pins [inlet outlet]
  (let [table (load-mdts inlet)
        data (reduce (fn [state item]
                       (assoc state
                              (get item "region")
                              {"icon" (-> (get item "icon")
                                          (clojure.string/split #" ")
                                          second)
                               "markerColor" (get item "marker color")
                               "iconColor" (get item "icon color")
                               "prefix" "fa"}))
                     {}
                     table)]
    (->> data
         json/generate-string
         (spit outlet))))

(defn main []
  (let [pins (load-mdts pins-md-file-name)]
    (convert-places places-md-file-name pins places-json-file-name)
    (convert-pins pins-md-file-name pins-json-file-name)))

(main)
