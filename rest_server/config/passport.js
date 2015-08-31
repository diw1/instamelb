// config/passport.js
// Hold PassportJS Strategies

var BasicStrategy = require('passport-http').BasicStrategy;

// HTTP Basic Authentication Strategy
module.exports = function (config, db, passport) {
    passport.use('basic', new BasicStrategy(
        function(userid, password, done) {
            // Query Database for Credentials match

            return done(null, true); // Successful Authentication
        }
    ));
}
