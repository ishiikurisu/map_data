require 'yaml'
require 'json'

# PINS
raw_pins = { }
File.open("raw_pins.yml", "r") do |fp|
  raw_pins = YAML.load fp.read
end
pins = { }
gen_to_pin = { }
raw_pins.each do |pin_key, pin_desc|
  pins[pin_key] = {
    "icon" => pin_desc["icon"].split(" ")[1],
    "markerColor" => pin_desc["marker_color"],
    "iconColor" => pin_desc["icon_color"],
    "prefix" => pin_desc["icon"].split(" ")[0],
  }
  if pin_desc.has_key? "gen"
    gen_to_pin[pin_desc["gen"]] = pin_key
  end
end
File.open("pins.json", "w") do |fp|
  fp.write pins.to_json
end

# PLACES
raw_places = []
File.open("raw_places.yml", "r") do |fp|
  raw_places = YAML.load fp.read
end
places = []
raw_places.each do |place|
  status = "default"
  if gen_to_pin.has_key? place["gen"]
    status = gen_to_pin[place["gen"]]
  end
  places << {
    "lat" => place["lat"],
    "lon" => place["lon"],
    "popup" => place["poke"],
    "status" => status,
  }
end
File.open("places.json", "w") do |fp|
  fp.write places.to_json
end

