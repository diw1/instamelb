// config/passport.js
// Hold PassportJS Strategies

var BasicStrategy = require('passport-http').BasicStrategy;

// HTTP Basic Authentication Strategy
module.exports = function (config, db, passport) {
    passport.use(new BasicStrategy(
        function(userid, password, done) {
            // Database function, check
            // db get user from table
            return done(null, true); // Stub, always authenticate
        }
    ));
}
