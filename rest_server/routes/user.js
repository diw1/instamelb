// routes/user.js
// Contains user routes

// Utility Modules
var path = require('path');
var log = require(path.join(__dirname, '..', 'utils', 'log.js'));
var auth = require(path.join(__dirname, '..', 'utils', 'auth.js'));

// Controller
var user_controller = require(path.join(__dirname, '..',
            'app', 'controllers', 'user-controller.js'));
// View
var user_view = require(path.join(__dirname, '..',
            'app', 'views', 'user-view.js'));

module.exports = function (config, app, db, passport) {
    
    // Logger
    var logger = log(config);

    user_controller = user_controller(config, db);
    user_view = user_view(config);

    // Get User Profile
    app.get('/users/:user_id', function (req, res) {
        logger("GET /users/:user_id");

        user_controller.getUser(req.param.user_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            user_view.getUser(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Get User Photos
    app.get('/users/:user_id/photos', function (req, res) {
        logger("GET /users/:user_id/photos");

        user_controller.getUserPhotos(req.param.user_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            user_view.getUserPhotos(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Get Self Feed
    app.get('/users/self/feed', function (req, res) {
        logger("GET /users/self/feed");

        user_controller.getSelfFeed(function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            user_view.getSelfFeed(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

}
