// app/views/index-view.js
// Index view, for misc routes

var validator = require('is-my-json-valid')

module.exports = function (config) { return {

    // GET User
    getUser: function (user_json, done) {

        return done(null, user_json);
    },

    // GET User Photos
    getUserPhotos: function (photos_json, done) {

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

