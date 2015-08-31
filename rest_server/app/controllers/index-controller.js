// app/controllers/index-controller.js
// Index controller, for misc routes

module.exports = function (config) { return {

    // GET Login
    getLogin: function (done) {
        var auth_success = { "authenticated": true };
        return done(null, auth_success);
    },
    
    // POST /register
    postRegister: function (register_json, done) {
        var register_response = { "registered": true };
        return done(null, register_response);
    }

}}

