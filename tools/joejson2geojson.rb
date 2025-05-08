#!/usr/bin/env ruby

require "json"

def joejson2geojson inlet, outlet
  map_data = JSON.parse(File.read(inlet))
  lat = map_data["lat"]
  lon = map_data["lon"]
  zoom = map_data["zoom"]
  features = map_data["places"].map do |place|
    {
      "type" => "Feature",
      "geometry" => {
        "type" => "Point",
        "coordinates" => [place["lon"], place["lat"]],
      },
      "properties" => {
        "center" => false,
        "popup" => place["popup"],
        "pin" => place["status"],
      },
    }
  end

  if !lat.nil? && !lon.nil? && !zoom.nil? 
    features << {
      "type" => "Feature",
      "geometry" => {
        "type" => "Point",
        "coordinates" => [lon, lat],
      },
      "properties" => {
        "center" => true,
        "zoom" => zoom,
      },
    }
  end

  File.write(outlet, JSON.pretty_generate({
    "type" => "FeatureCollection",
    "features" => features,
    "metadata" => {
      "pins" => map_data["pins"] || {},
    },
  }))
end

def main args
  args.each do |inlet|
    outlet = inlet.gsub ".json", ".geojson"
    joejson2geojson inlet, outlet
  end
end

if $0 == __FILE__
  # should be called as
  # ruby tools/joejson2geojson.rb */map_data.json
  main ARGV
end

