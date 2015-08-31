// app/views/index-view.js
// Index view, for misc routes

module.exports = function (config) { return {

    // GET Login
    getLogin: function (login_response, done) {
        return done(null, login_response);
    },
    
    // POST /register
    postRegister: function (register_response, done) {
        return done(null, register_response);
    }

}}

