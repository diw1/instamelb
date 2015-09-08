// app/views/index-view.js
// Index view, for misc routes

var validator = require('is-my-json-valid')

module.exports = function (config) { return {

    // GET Photo
    getPhoto: function (photo_json, done) {

        // Photo Response JSON Structure
        var validatePhotoResponseJSON = validator({
            require: true,
            type: 'object',
            properties: {
                uploaded: {
                    required: true,
                    type: 'boolean'
                },
                photo_id: {
                    required: true,
                    type: 'number'
                },
                photo_image: {
                    required: true,
                    type: 'string'
                },
                photo_caption: {
                    required: true,
                    type: 'string'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validatePhotoResponseJSON(photo_json);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, photo_json);
    },

    postPhoto: function (photo_json, done) {

        // Photo Response JSON Structure
        var validatePhotoResponseJSON = validator({
            require: true,
            type: 'object',
            properties: {
                photo_id: {
                    required: true,
                    type: 'number'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validatePhotoResponseJSON(photo_json);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, photo_json);
    }

}}

