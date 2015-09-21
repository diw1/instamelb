// app/controllers/photo-controller.js
// Photos controller, for photo routes

var validator = require('is-my-json-valid')

// Specific Error messages
var ValidationError = require("sequelize").ValidationError;

module.exports = function (config, db) { return {

    // GET User
    getUser: function (auth_user_id, user_id, done) {

        if (user_id == "self") {
            user_id = auth_user_id;
        }

        // Cast to Number
        user_id = Number(user_id);

        // if NaN
        if (isNaN(user_id)) {
                var error = {"status":400,"body":{"message":"user_id invalid."}}
                return done(error);
        }

        db.Users.findOne({
            where: {id: user_id}
        }).then(function(result) {

            // No result found
            if (!result) {
                var error = {"status": 403,"body":{"message":"User not found."}}
                return done(error);
            }

            var user = result.dataValues;

            var user_json = {}
            user_json.user_id = user.id;
            user_json.username = user.username;
            user_json.email = user.email;
            user_json.profile_image = "http://images.instamelb.pinkpineapple.me/1.jpg";
            user_json.counts = {
                "photos": 0,
                "follows": 0,
                "followed_by": 0
            }

            return done(null, user_json);

        }).catch(function(error) {
                var error = {"status": 500,"body":{ message:"Internal Server Error."}}
                return done(error);
        });

    },

    // Get User Photos
    getUserPhotos: function (auth_user_id, user_id, done) {

        if (user_id == "self") {
            user_id = auth_user_id;
        }

        // Cast to Number
        user_id = Number(user_id);

        // if NaN
        if (isNaN(user_id)) {
                var error = {"status":400,"body":{"message":"user_id invalid."}}
                return done(error);
        }

        db.Photos.findAll({
            where: {user_id: user_id}
        }).then(function(result) {

            var photos_json = { "photos": [] }

            // If there are results, add to photos
            if (result) {

                for (var i=0;i < result.length;i++) {

                    var photo_object = result[i].dataValues;

                    var timestamp = new Date(photo_object.created_at).getTime();

                    var photo_json = {};
                    photo_json.photo_id = photo_object.id;
                    photo_json.photo_image = photo_object.url;
                    photo_json.photo_caption = photo_object.caption;
                    photo_json.comments = { count: 0 };
                    photo_json.likes = { count: 0 };
                    photo_json.location = {
                        longitude: photo_object.longitude,
                        latitude: photo_object.latitude
                    };

                    photos_json.photos.push(photo_json);
                }
            }
    
            return done(null, photos_json);

        }).catch(function(error) {
                var error = {"status": 500,"body":{ message:"Internal Server Error."}}
                return done(error);
        });

    },

    // GET Search Users
    getSearchUsers: function (query_string, is_suggested, done) {

        // Turn to empty string if null/undefined
        if (!query_string) {
            query_string = ""
        }

        search_str = "%" + query_string + "%"

        db.Users.findAll({
            where: {username: {like: search_str}}
        }).then(function(result) {

            var result_json = { "result": [] }

            if (result) {

                for (var i=0;i < result.length;i++) {
                    
                    var user_object = result[i].dataValues;

                    var user_json = {};
                    user_json.user_id = user_object.id;
                    user_json.username = user_object.username;
                    user_json.profile_image = "http://images.instamelb.pinkpineapple.me/1.jpg";

                    result_json.result.push(user_json);
                }
            }

            return done(null, result_json);
        });
        
    },

    // GET Self Feed
    getSelfFeed: function (done) {

        var feed_json = {
            "feed": [
                {
                    "photo_id": 1,
                    "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
                    "photo_caption": "Good Photo",
                    "timestamp": 1442805159890,
                    "comments": {
                        "count": 127
                    },
                    "likes": {
                        "count": 2045
                    },
                    "location": {
                        "longitude": 123.45,
                        "latitude": 234.56
                    },
                    "user": {
                        "user_id": 1,
                        "username": "Pheo",
                        "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "photo_id": 1,
                    "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
                    "photo_caption": "Shit Photo",
                    "timestamp": 1442805159890,
                    "comments": {
                        "count": 127
                    },
                    "likes": {
                        "count": 2045
                    },
                    "location": {
                        "longitude": 123.45,
                        "latitude": 234.56
                    },
                    "user": {
                        "user_id": 1,
                        "username": "Pheo",
                        "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    }
                },
                {
                    "photo_id": 1,
                    "photo_image": "http://images.instamelb.pinkpineapple.me/1.jpg",
                    "photo_caption": "Okay Photo",
                    "timestamp": 1442805159890,
                    "comments": {
                        "count": 127
                    },
                    "likes": {
                        "count": 2045
                    },
                    "location": {
                        "longitude": 123.45,
                        "latitude": 234.56
                    },
                    "user": {
                        "user_id": 1,
                        "username": "Pheo",
                        "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
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
    },

    // GET User Follows
    getUserFollows: function (auth_user_id, user_id, done) {

        if (user_id == "self") {
            user_id = auth_user_id;
        }

        // Cast to Number
        user_id = Number(user_id);

        // if NaN
        if (isNaN(user_id)) {
                var error = {"status":400,"body":{"message":"user_id invalid."}}
                return done(error);
        }

        db.Follows.findAll({
            include: [{model: db.Users, as: 'user_followed'}],
            where: {user_id: user_id}
        }).then(function(result) {

            var follows_json = { "follows": [] };

            for (var i=0;i < result.length;i++) {
                var followed_user_object = result[i].user_followed.dataValues;
                var followed_user = {}

                followed_user.user_id = followed_user_object.id;
                followed_user.username = followed_user_object.username;
                followed_user.profile_image = "http://images.instamelb.pinkpineapple.me/1.jpg";

                follows_json.follows.push(followed_user);
            }

            return done(null, follows_json);

        });
    },

    // GET User Followers
    getUserFollowers: function (auth_user_id, user_id, done) {

        if (user_id == "self") {
            user_id = auth_user_id;
        }

        // Cast to Number
        user_id = Number(user_id);

        // if NaN
        if (isNaN(user_id)) {
                var error = {"status":400,"body":{"message":"user_id invalid."}}
                return done(error);
        }

        db.Follows.findAll({
            include: [{model: db.Users, as: 'user_following'}],
            where: {follow_user_id: user_id}
        }).then(function(result) {

            var followers_json = { "followers": [] };

            for (var i=0;i < result.length;i++) {
                var follower_user_object = result[i].user_following.dataValues;
                var follower_user = {}

                follower_user.user_id = follower_user_object.id;
                follower_user.username = follower_user_object.username;
                follower_user.profile_image = "http://images.instamelb.pinkpineapple.me/1.jpg";

                followers_json.followers.push(follower_user);
            }

            return done(null, followers_json);

        });

    },

    // POST Relationship
    postRelationship: function (auth_user_id, user_id, action_json, done) {

        // Cast to Number
        user_id = Number(user_id);

        // if NaN
        if (isNaN(user_id)) {
                var error = {"status":400,"body":{"message":"user_id invalid."}}
                return done(error);
        }
        
        var validateActionJSON = validator({
            required: true,
            type: 'object',
            properties: {
                action: {
                    required: true,
                    type: 'string'
                }
            }
        });

        // JSON Invalid?
        var json_valid = validateActionJSON(action_json);
        if (!json_valid) {
            var error_json = { "status": 500,
                "body": { "error": "Server response JSON invalid." } }
            return done(error_json);
        }

        // Follow User
        if (action_json.action == "follow") {

            db.Follows.upsert({
                user_id: auth_user_id,
                follow_user_id: user_id,
            }, {}).then(function(result) {

                var response_json = { "action": action_json.action };
                
                if (result) {
                    // True, so started following
                    response_json.modified = true;
                } else {
                    // False, so already exists
                    response_json.modified = false;
                    response_json.message = "Already following User";
                }

                return done(null, response_json);

            });

        // Unfollow User
        } else if (action_json.action == "unfollow") {
            
            db.Follows.destroy({
                where: {
                    user_id: auth_user_id,
                    follow_user_id: user_id
                }
            }).then(function(result) {

                var response_json = { "action": action_json.action };
        
                if (result > 0) {
                    // Something was deleted
                    response_json.modified = true;
                } else {
                    // Nothing was deleted
                    response_json.modified = false;
                    response_json.message = "No relationship Exists";
                }

                return done(null, response_json);

            });
        } else {
            var error_json = { "status": 400,
                "body": { "error": "Unrecognized action '"+action_json.action+"'"} }
            return done(error_json);
        }
    }

}}

