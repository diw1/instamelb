// app/views/index-view.js
// Index view, for misc routes

var validator = require('is-my-json-valid')

module.exports = function (config) { return {

    // GET Photo
    getPhoto: function (photo_json, done) {

        // Photo Response JSON Structure
        var validatePhotoResponseJSON = validator({
            required: true,
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
            required: true,
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
    },

    getComments: function (comments_json, done) {

        return done(null, comments_json);

    },

    postComment: function (post_comment_response, done) {

        var validatePostCommentResponseJSON = validator({
            required: true,
            type: 'object',
            properties: {
                posted: {
                    required: true,
                    type: 'boolean'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validatePostCommentResponseJSON(post_comment_response);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, post_comment_response);

    },

    deleteComment: function (delete_comment_response, done) {

        var validateDeleteCommentResponseJSON = validator({
            required: true,
            type: 'object',
            properties: {
                deleted: {
                    required: true,
                    type: 'boolean'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validateDeleteCommentResponseJSON(delete_comment_response);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, delete_comment_response);

    },

    getLikes: function (likes_json, done) {

        return done(null, likes_json);

    },

    postLike: function (post_like_response, done) {

        return done(null, post_like_response);

    },

    deleteLike: function (delete_like_response, done) {

        return done(null, delete_like_response);

    }

}}

