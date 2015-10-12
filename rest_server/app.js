// app.js Entry point

/* Import Modules */

// General Util Modules
var path = require('path');
var async = require('async');

// App Modules
var http = require('http');
var passport = require('passport');
var express = require('express');
var bodyParser = require('body-parser');

// Sequelize
var Sequelize = require("sequelize");

// Logging
var log = require(path.join(__dirname, 'utils', 'log.js'));

module.exports = function (config, done_app) {

    // App wrapper to be returned
    var app_wrapper;

    // Database object
    var db;

    // Logger
    var logger = log(config);

    // Perform app initialization
    async.series([
        // Database Setup
        function (callback) {

            // Connect Sequelize to Database
            logger("[Database] Initializing Database @ "
                    + config.db.host + ":" + config.db.port + "...");
            var sequelize = new Sequelize(config.db.name,
                    config.db.username, config.db.password,
                    {
                        dialect: "mariadb", host: config.db.host,
                        port: config.db.port, logging: false,
                        define: { underscored: true }
                    });


            // Import Database Models
            logger("[Database] Importing Sequelize ORM Models...");
            db = require(path.join(__dirname, 'app',
                        'models', 'models.js'))(sequelize)

            // Synchronize Models to DB
            logger("[Database] Synchronizing Models to Database");
            sequelize.sync({ force: config.db.seed }).then(function(){
                    logger('[Database] Sequelize synchronized with database!');

                    // Database Seeding
                    if (config.db.seed) {
                        logger("[Database] Seeding Database. Tables have been dropped.");
                    } else {
                        logger("[Database] Not Seeding Database.");
                    }

                    return callback(null);
            });
        },
        // Other stuff i guess
        function (callback) {
            return callback(null);
        }], function (err, results) {

            // App Setup
            var server;
            var app = express();

            // Body Parsing (necessary to get body JSON)
            app.use(bodyParser.json({ limit: '20mb' }));
            app.use(bodyParser.urlencoded({ extended: true }));

            // PassportJS Strategy Setup
            require(path.join(__dirname, 'config', 'passport.js'))(config, db, passport)

            // Set up Routes
            require(path.join(__dirname, 'routes', 'index.js'))(config, app, db, passport);
            require(path.join(__dirname, 'routes', 'photo.js'))(config, app, db, passport);
            require(path.join(__dirname, 'routes', 'user.js'))(config, app, db, passport);

            // HTTP Server
            server = http.createServer(app);

            // Export App
            app_wrapper = {
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

            // Return App Wrapper
            return done_app(app_wrapper);
        }
    );
}

