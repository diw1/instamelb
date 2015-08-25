// routes/index.js
// Contains root, other misc routes

var path = require('path');
var log = require(path.join(__dirname, '..', 'utils', 'log.js'))

module.exports = function (config, app, db, passport) {
    
    var logger = log(config);

    // Root
    app.get('/', function (req, res) {
        logger("GET /");

        res.send({ "name": "Instamelb REST API" });
    });

};
