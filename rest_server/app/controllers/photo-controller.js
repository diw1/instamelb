// app/controllers/photo-controller.js
// Photos controller, for photo routes

var validator = require('is-my-json-valid')

module.exports = function (config, db) { return {

    // GET Photo
    getPhoto: function (photo_id, done) {

        // Grab photo from database
        db.Photos.findOne({
            include: [{model: db.Users, as: 'photo_owner'}],
            where: { id: photo_id }
        }).then(function(result) {

            // If nothing found
            if (!result) {
                var error = {"status":404,"body":{"message":"Photo not found."}}
                return done(error);
            }

            var photo_object = result.dataValues;
            var photo_owner_object = result.photo_owner.dataValues;

            var photo_json = {
                "photo_id": photo_object.id,
                "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
                "photo_caption": photo_object.caption,
                "location": {
                    "longitude": photo_object.longitude,
                    "latitude": photo_object.latitude
                },
                "user": {
                    "user_id": photo_owner_object.id,
                    "username": photo_owner_object.username,
                    "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                },
                "comments": {
                    "count": 0
                },
                "likes": {
                    "count": 0
                }
            }

            return done(null, photo_json);
        });
    },

    // POST Photo
    postPhoto: function (new_photo_json, done) {
        
        // New Photo JSON Structure
        var validatePhotoRequestJSON = validator({
            required: true,
            type: 'object',
            properties: {
                caption: {
                    required: true,
                    type: 'string'
                },
                image: {
                    required: true,
                    type: 'string'
                },
                longitude: {
                    required: false,
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

    },

    getComments: function (photo_id, done) {

        var comments_json = {
            "comments": [
                {
                    "comment_id": 1,
                    "created_at": 1280780324,
                    "updated_at": 1234124096,
                    "location": {
                        "longitude": 123.45,
                        "latitude": 123.45
                    },
                    "text": "Really amazing photo!",
                    "from": {
                        "user_id": 1,
                        "username": "Pheo",
                        "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                }
            ]
        }

        return done(null, comments_json);
    },

    postComment: function (photo_id, new_comment_json, done) {

        var validatePostCommentJSON = validator({
            required: true,
            type: 'object',
            properties: {
                text: {
                    required: true,
                    type: 'string'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validatePostCommentJSON(new_comment_json);
        if (!json_valid) {
            var error_json = { "status": 400,
                "body": { "error": "Request JSON invalid." } }
            return done(error_json);
        }

        var post_comment_reponse = {
            "posted": true
        }

        return done(null, post_comment_reponse);

    },

    deleteComment: function (photo_id, comment_id, done) {

        var delete_comment_response = {
            "deleted": true
        }

        return done(null, delete_comment_response);

    },

    getLikes: function (photo_id, done) {

        var likes_json = {
            "likes": [
                {
                    "user_id": 1,
                    "username": "Pheo",
                    "timestamp": 145234325243324
                }
            ]
        }

        return done(null, likes_json);

    },

    postLike: function (photo_id, done) {

        var post_like_response = {
            "liked": true
        }

        return done(null, post_like_response);

    },

    deleteLike: function (photo_id, done) {

        var delete_like_response = {
            "deleted": true
        }

        return done(null, delete_like_response);

    }

}}

