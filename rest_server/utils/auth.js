// utils/auth.js
// Authentication Middleware/Wrapper to keep routes clean

module.exports = function (passport) {
    return passport.authenticate('basic', { session: false });
}
