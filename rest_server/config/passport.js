// config/passport.js
// Hold PassportJS Strategies

var BasicStrategy = require('passport-http').BasicStrategy;

// HTTP Basic Authentication Strategy
module.exports = function (config, db, passport) {
    passport.use('basic', new BasicStrategy(
        function(userid, password, done) {
            // Query Database for Credentials match
            db.Users.findOne({
                where: {username: userid, password: password}
            }).then(function(user) {
                // Check that user account exists
                if (user) {
                    return done(null, true); // Successful Authentication
                } else {
                    return done(null, false); // Unsuccessful Authentication
                }
            })
        }
    ));
}
