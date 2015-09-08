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
    
}}

