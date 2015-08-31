// app/views/index-view.js
// Index view, for misc routes

var validator = require('is-my-json-valid')

module.exports = function (config) { return {

    // GET Login
    getLogin: function (login_response, done) {

        // Login Response JSON Structure
        var validateLoginResponseJSON = validator({
            require: true,
            type: 'object',
            properties: {
                authenticated: {
                    required: true,
                    type: 'boolean'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validateLoginResponseJSON(login_response);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, login_response);
    },
    
    // POST /register
    postRegister: function (register_response, done) {

        // Register Response JSON Structure
        var validateRegisterResponseJSON = validator({
            require: true,
            type: 'object',
            properties: {
                registered: {
                    required: true,
                    type: 'boolean'
                },
                error: {
                    required: false,
                    type: 'string'
                }
            }
        });

        // JSON Invalid?
        json_valid = validateRegisterResponseJSON(register_response);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        // All good, go ahead
        return done(null, register_response);
    }

}}

