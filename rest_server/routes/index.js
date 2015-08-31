// routes/index.js
// Contains root, other misc routes

// Utility Modules
var path = require('path');
var log = require(path.join(__dirname, '..', 'utils', 'log.js'));
var auth = require(path.join(__dirname, '..', 'utils', 'auth.js'));

// Controller
var index_controller = require(path.join(__dirname, '..',
            'app', 'controllers', 'index-controller.js'));
// View
var index_view = require(path.join(__dirname, '..',
            'app', 'views', 'index-view.js'));

module.exports = function (config, app, db, passport) {
    
    // Logger
    var logger = log(config);

    index_controller = index_controller(config);
    index_view = index_view(config);

    // Root
    app.get('/', function (req, res) {
        logger("GET /");
        res.send({ "name": "Instamelb REST API" });
    });

    // User Login (Auth Check)
    app.get('/login', auth(passport), function (req, res) {
        logger("GET /login");

        index_controller.getLogin(function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            index_view.getLogin(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // User Registration
    app.post('/register', function (req, res) {
        logger("POST /register");

        var register_json = req.body // Body contains Registration JSON

        index_controller.postRegister(
                register_json, 
                function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            index_view.postRegister(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

}

