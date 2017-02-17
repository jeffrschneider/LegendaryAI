"use strict";
const spawn = require("child_process").spawn;
const https = require("https");
const fs = require('fs');
const databaseConfig = JSON.parse(fs.readFileSync(process.argv[2], "utf8"));
const launcherString =
  ("java CorrectionInstance " + ((databaseConfig.user + " ") + (databaseConfig.password + " "))) +
  ((databaseConfig.host + " ") + (databaseConfig.database + " "));
const serverConfig = {
  key: fs.readFileSync('./key.pem'),
  cert: fs.readFileSync('./cert.pem')
};
const errString = "{'statusCode': -1, 'err':'unable to parse request'}";
https.createServer(serverConfig, function(request, response){
  var body = [];
  request.on('error', function(err) {
    response.write(errString);
    response.end();
  }).on('data', function(chunk) {
    body.push(chunk);
  }).on('end', function() {
    body = Buffer.concat(body).toString();
    try {
      JSON.parse(body);
      var correction = "";
      const proc = spawn(launcherString + body.flawedString);
      proc.stderr.on("data", function(data){
        response.write(errString);
        response.end();
      });
      proc.stdout.on("data", function(data){
        correction = correction + data;
      });
      proc.on('close', function(term){
        var responseData = {
          statusCode: 1,
          result: correction
        };
        response.write(JSON.stringify(responseData));
        response.end();
      });
    } catch (e) {
      response.write(errString);
      response.end();
    }
  });
}).listen(8080);
