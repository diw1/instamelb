// app/views/index-view.js
// Index view, for misc routes

var validator = require('is-my-json-valid')

module.exports = function (config) { return {

    // GET User
    getUser: function (user_json, done) {

        var validateUserJSON = validator({
            required: true,
            type: 'object',
            properties: {
                user_id: {
                    required: true,
                    type: 'number'
                },
                username: {
                    required: true,
                    type: 'string'
                },
                email: {
                    required: true,
                    type: 'string'
                },
                profile_image: {
                    required: true,
                    type: 'string'
                },
                counts: {
                    properties: {
                        photos: {
                            required: true,
                            type: 'number'
                        },
                        follows: {
                            required: true,
                            type: 'number'
                        },
                        followed_by: {
                            required: true,
                            type: 'number'
                        }
                    }
                }
            }
        });

        // JSON Invalid?
        var json_valid = validateUserJSON(user_json);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, user_json);
    },

    // GET User Photos
    getUserPhotos: function (photos_json, done) {

        var validateUserPhotosJSON = validator({
            required: true,
            type: 'object',
            properties: {
                photos: {
                    required: true,
                    type: 'array'
                }
            }

        });

        // JSON Invalid?
        var json_valid = validateUserPhotosJSON(photos_json);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        return done(null, photos_json);
    },

    // GET Search Users
    getSearchUsers: function (users_json, done) {
        
        return done(null, users_json);
    },

    // GET Self Feed
    getSelfFeed: function (feed_json, done) {

        return done(null, feed_json);
    },
    
    // GET Follows Feed
    getFollowsFeed: function (feed_json, done) {

        return done(null, feed_json);
    },

    // GET User Follows
    getUserFollows: function (follows_json, done) {

        return done(null, follows_json);
    },

    // GET User Followers
    getUserFollowers: function (followers_json, done) {

        return done(null, followers_json);
    },

    // POST Relationship
    postRelationship: function (response_json, done) {
        
        return done(null, response_json);
    }

}}

