// routes/photo.js
// Contains photo routes

// Utility Modules
var path = require('path');
var log = require(path.join(__dirname, '..', 'utils', 'log.js'));
var auth = require(path.join(__dirname, '..', 'utils', 'auth.js'));

// Controller
var photo_controller = require(path.join(__dirname, '..',
            'app', 'controllers', 'photo-controller.js'));
// View
var photo_view = require(path.join(__dirname, '..',
            'app', 'views', 'photo-view.js'));

module.exports = function (config, app, db, passport) {
    
    // Logger
    var logger = log(config);

    photo_controller = photo_controller(config, db);
    photo_view = photo_view(config);

    // Get Photo
    app.get('/photo/:photo_id', function (req, res) {
        logger("GET /photo/:photo_id");

        photo_controller.getPhoto(req.param.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.getPhoto(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Post Photo
    app.post('/photo', function (req, res) {
        logger("POST /photo");

        photo_controller.postPhoto(req.body, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.postPhoto(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

}

