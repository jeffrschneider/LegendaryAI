"use strict";
const mysql = require("mysql");
const fs = require("fs");
const decimal = require("decimal");
const rawFile = fs.readFileSync(process.argv[2], "utf8");
const config = JSON.parse(rawFile);
fs.readFile("./n-gram-2.csv", "utf8", function(err, data){
  if (err) {
    console.dir(err);
    return;
  }
  var total = new decimal("0");
  const k = data.split("\r");
  var rows = k.slice(0, k.length-1);
  for (var index = 0; index < rows.length; index++){
    const points = rows[index].split("\n").join("").split("\'").join("\\\'").split("\"").join("\\\"").split("\t").join("").split(" ").join("").split(",");
    total = total.add(points[0]);
    rows[index] = points;
  }
  console.log(total.toString());
  for (var index = 0; index < rows.length; index++){
    const row = rows[index];
    const percentage = (new decimal(row[0])).div(total).toString();
    rows[index] = "INSERT INTO `2Grams` (`Count`, `Word1`, `Word2`, `Percent`) VALUES(\'" + row[0] + "\', \'" + row[1] + "\', \'" + row[2] + "\', " + percentage + ");";
  }
  const completedQueryResult = "USE `NGrams`;\n" + rows.join("\n");
  fs.writeFile("./job2.sql", completedQueryResult, function(err4){
    if (err4) {
      console.dir(err4);
    }
    return;
  });
});
