// app/controllers/photo-controller.js
// Photos controller, for photo routes

var validator = require('is-my-json-valid')

module.exports = function (config, db) { return {

    // GET Photo
    getPhoto: function (photo_id, done) {

        var photo_json = {
            "photo_id": 1,
            "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
            "photo_caption": "Good Photo",
            "location": {
                "longitude": 123.45,
                "latitude": 234.56
            },
            "user": {
                "user_id": 1,
                "username": "Pheo",
                "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
            },
            "comments": {
                "count": 127
            },
            "likes": {
                "count": 2045
            }
        }

        return done(null, photo_json);
    },

    // POST Photo
    postPhoto: function (new_photo_json, done) {
        
        // New Photo JSON Structure
        var validatePhotoRequestJSON = validator({
            require: true,
            type: 'object',
            properties: {
                caption: {
                    required: true,
                    type: 'string'
                },
                image: {
                    require: true,
                    type: 'string'
                },
                longitude: {
                    require: false,
                    type: 'number'
                },
                latitude: {
                    required: false,
                    type: 'number'
                }
            }
        });         

        // JSON Invalid?
        var json_valid = validatePhotoRequestJSON(new_photo_json);
        if (!json_valid) {
            var error_json = { "status": 400,
                "body": { "error": "Request JSON invalid." } }
            return done(error_json);
        }

        var photo_json = {
            "uploaded": true,
            "photo_id": 1,
            "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
            "photo_caption": "Good Photo",
            "user": {
                "user_id": 1,
                "username": "Pheo",
                "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
            },
            "location": {
                "longitude": 123.45,
                "latitude": 234.56
            }
        }

        return done(null, photo_json);

    }

}}

