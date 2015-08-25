// app.js Entry point

var http = require('http');
var path = require('path');
var async = require('async');

var passport = require('passport');
var express = require('express');
var bodyParser = require('body-parser');

var log = require(path.join(__dirname, 'utils', 'log.js'));

module.exports = function (config) {

    // Logger
    var logger = log(config);

    // Database Stuff
    var db = null;
    logger("[Database] Setup Complete!");

    // App Setup
    var server;
    var app = express();

    // PassportJS Strategy Setup
    require(path.join(__dirname, 'config', 'passport.js'))(config, db, passport)

    // Body Parsing / CORS
    app.use(bodyParser.json({ limit: '5mb' }));
    app.use(bodyParser.urlencoded({ extended: true }));
    app.use(function(req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept");
        next();
    });

    // Set up Routes
    require(path.join(__dirname, 'routes', 'index.js'))(config, app, db, passport);

    server = http.createServer(app);

    // Export App
    return {
        // Start Server
        start: function () {
            server.listen(config.server.port, function () {
                var port = server.address().port;
                logger("[App] Server Running @ localhost:" + port.toString());
            });
        },
        // Close Server
        close: function () {
            var port = server.address().port;
            logger("[App] Server Shutting Down..." + port.toString());
            server.close();
        }
    }
}
