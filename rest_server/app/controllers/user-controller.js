// app/controllers/photo-controller.js
// Photos controller, for photo routes

var validator = require('is-my-json-valid')

module.exports = function (config, db) { return {

    // GET User
    getUser: function (user_id, done) {

        var user_json = {
            "user_id": 1,
            "username": "Pheo",
            "email": "pheo@email.com",
            "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
            "counts": {
                "photos": 1320,
                "follows": 420,
                "followed_by": 3410
            }
        }

        return done(null, user_json);
    },

    // Get User Photos
    getUserPhotos: function (user_id, done) {

        var photos_json = {
            "photos": [
                {
                    "photo_id": 1,
                    "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
                    "photo_caption": "Good Photo",
                    "comments": {
                        "count": 127
                    },
                    "likes": {
                        "count": 2045
                    },
                    "location": {
                        "longitude": 123.45,
                        "latitude": 234.56
                    }
                }
            ]
        }

        return done(null, photos_json);
    },

    // GET Search Users
    getSearchUsers: function (query_string, is_suggested, done) {
        
        var users_json = {
            "result": [
                {
                    "user_id": 1,
                    "username": "Pheo",
                    "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                }
            ]
        }

        return done(null, users_json);
    },

    // GET Self Feed
    getSelfFeed: function (done) {

        var feed_json = {
            "feed": [
                {
                    "event": "comment",
                    "message": "Pheo commented on A's Photo.",
                    "user_commenting": {
                        "user_id": 1,
                        "username": "Pheo"
                    },
                    "user_commented": {
                        "user_id": 2,
                        "username": "A"
                    },
                    "photo": {
                        "photo_id": 1,
                        "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "event": "like",
                    "message": "Pheo liked A's Photo.",
                    "user_liking": {
                        "user_id": 1,
                        "username": "Pheo"
                    },
                    "user_liked": {
                        "user_id": 2,
                        "username": "A"
                    },
                    "photo": {
                        "photo_id": 1,
                        "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "event": "follow",
                    "message": "Pheo is following A.",
                    "user_following": {
                        "user_id": 1,
                        "username": "Pheo"
                    },
                    "user_followed": {
                        "user_id": 2,
                        "username": "A"
                    }
                }
            ]
        }

        return done(null, feed_json);
    },

    getFollowsFeed: function (done) {

        var feed_json = {
            "feed": [
                {
                    "event": "comment",
                    "message": "B commented on A's Photo.",
                    "user_commenting": {
                        "user_id": 3,
                        "username": "B"
                    },
                    "user_commented": {
                        "user_id": 2,
                        "username": "A"
                    },
                    "photo": {
                        "photo_id": 1,
                        "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "event": "like",
                    "message": "B liked A's Photo.",
                    "user_liking": {
                        "user_id": 3,
                        "username": "B"
                    },
                    "user_liked": {
                        "user_id": 2,
                        "username": "A"
                    },
                    "photo": {
                        "photo_id": 1,
                        "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "event": "follow",
                    "message": "B is following A.",
                    "user_following": {
                        "user_id": 3,
                        "username": "B"
                    },
                    "user_followed": {
                        "user_id": 2,
                        "username": "A"
                    }
                }
            ]
        } 

        return done(null, feed_json);
    }

}}

