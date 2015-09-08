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

    // GET Self Feed
    getSelfFeed: function (feed_json, done) {

        return done(null, feed_json);
    }

}}
