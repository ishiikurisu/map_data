import json


def main():
    inlet = {}
    with open("index.blog.json", "r") as fp:
        inlet = json.loads(fp.read())

    for entry in inlet:
        folder = entry["path"]
        lat = entry["lat"]
        lon = entry["lon"]
        zoom = entry["zoom"]

        filename = folder + "/map_data.json"
        outlet = {}
        with open(filename, "r") as fp:
            outlet = json.loads(fp.read())

        outlet["lat"] = lat
        outlet["lon"] = lon
        outlet["zoom"] = zoom

        with open(filename, "w") as fp:
            fp.write(json.dumps(outlet, indent=2))


if __name__ == "__main__":
    main()

