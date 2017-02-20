"use strict";
const exec = require("child_process").exec;
const https = require("https");
const fs = require('fs');
const databaseConfig = JSON.parse(fs.readFileSync(process.argv[2], "utf8"));
const launcherString =
  ("java -jar usableInstance.jar " + ((databaseConfig.user + " ") + (databaseConfig.password + " "))) +
  ((databaseConfig.host + " ") + (databaseConfig.database + " "));
const serverConfig = {
  key: fs.readFileSync('./key.pem'),
  cert: fs.readFileSync('./cert.pem'),
  passphrase: "D3@dH00k3r"
};
const errString = "{'statusCode': -1, 'err':'unable to parse request'}";
https.createServer(serverConfig, function(request, response){
  var body = [];
  request.on('error', function(err) {
    if (!(response.finished)) {
      response.write(errString);
      response.end();
    }
    console.log("network error");
  }).on('data', function(chunk) {
    body.push(chunk);
  }).on('end', function() {
    body = Buffer.concat(body).toString();
    try {
      const input = JSON.parse(body);
      var correction = "";
      const spawnString = launcherString + input.flawedString.split("{").join(" ").split("}").join(" ");
      exec(spawnString, function(error, stdout, stderr){
        if (error){
          if (!(response.finished)) {
            response.write(errString);
            response.end();
          }
          console.log("reciever error: " + spawnString);
          console.log(error);
          return;
        }
        if (stderr){
          if (!(response.finished)) {
            response.write(errString);
            response.end();
          }
          console.log("reciever error: " + spawnString);
          console.log(stderr);
          return;
        }
        var responseData = {
          statusCode: 1,
          result: stdout
        };
        if (!(response.finished)) {
          response.write(JSON.stringify(responseData));
          response.end();
        }
      });
    } catch (e) {
      if (!(response.finished)) {
        response.write(errString);
        response.end();
      }
      console.log("JSON parsing error");
    }
  });
}).listen(8080);
