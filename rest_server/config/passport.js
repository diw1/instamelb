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

                    var user_json = {}
                    user_json.id = user.dataValues.id;
                    user_json.username = user.dataValues.username;
                    user_json.email = user.dataValues.email;

                    return done(null, user_json); // Successful Authentication
                } else {
                    return done(null, null); // Unsuccessful Authentication
                }
            })
        }
    ));
}
