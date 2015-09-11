// app/controllers/index-controller.js
// Index controller, for misc routes

var validator = require('is-my-json-valid')

// Specific Error messages
var ValidationError = require("sequelize").ValidationError;

module.exports = function (config, db) { return {

    // GET Login
    getLogin: function (user_object, done) {
        var auth_success = { "authenticated": true };
        auth_success.user = {}
        auth_success.user.user_id = user_object.id;
        auth_success.user.username = user_object.username;
        auth_success.user.email = user_object.email;

        return done(null, auth_success);
    },
    
    // POST /register
    postRegister: function (register_json, done) {

        // Register JSON Structure
        var validateRegisterJSON = validator({
            required: true,
            type: 'object',
            properties: {
                username: {
                    required: true,
                    type: 'string'
                },
                password: {
                    required: true,
                    type: 'string'
                },
                email: {
                    required: true,
                    type: 'string'
                }
            }
        });

        console.log(register_json);
        // JSON Invalid?
        json_valid = validateRegisterJSON(register_json);
        if (!json_valid) {
            var error_json = { "status": 400,
                "body": { "error": "Registration JSON Invalid." } }
            return done(error_json);
        }

        // JSON Valid, create user
        db.Users.create({
            username: register_json.username,
            password: register_json.password,
            email: register_json.email
        }).then(function(new_user) {
            var user_object = new_user.dataValues;

            var register_response = { "registered": true };
            register_response.user = {};
            register_response.user.user_id = user_object.id;
            register_response.user.username = user_object.username;
            register_response.user.email = user_object.email;

            return done(null, register_response);
        }).catch(function(errors) {
            // Validation Error (Non-unique username/email)
            if (errors instanceof ValidationError) {
                var error_message_string = "";
                for (i=0; i < errors.errors.length; i++) {
                    error_message_string += errors.errors[i].message + ". ";
                }
                var register_response = {
                    "registered": false,
                    "error": error_message_string
                }
                return done (null, register_response);
            } else {
                var error_json;
                error_json = { "status": 500,
                    "body": { "error": errors.message } }
                return done(error_json);
            }
        });
    }
}}

