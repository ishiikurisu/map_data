#!/usr/bin/env ruby

require "json"

def include_original_data inlet
  inlet.collect do |item|
    item.update({"original_date" => item["creation_date"]})
  end
end

def main
  inlet = ""
  while (line = gets) != nil
    inlet += line
  end

  puts(JSON.pretty_generate(include_original_data(JSON.parse(inlet))))
end

if $0 == __FILE__
  # ruby tools/include_original_data < index.blog.json
  main
end

