// app/controllers/photo-controller.js
// Photos controller, for photo routes

var validator = require('is-my-json-valid')
var fs = require('fs')
var path = require('path')
var uuid = require('node-uuid')
var imageType = require('image-type')

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

            var timestamp = new Date(photo_object.created_at).getTime();

            var photo_json = {
                "photo_id": photo_object.id,
                "photo_image": photo_object.url,
                "photo_caption": photo_object.caption,
                "timestamp": timestamp,
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
    postPhoto: function (auth_user_id, new_photo_json, done) {
        
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
                image_thumbnail: {
                    required: true,
                    type: 'string'
                },
                longitude: {
                    required: false,
                    type: 'string'
                },
                latitude: {
                    required: false,
                    type: 'string'
                }
            }
        });         

        // JSON Invalid?
        var json_valid = validatePhotoRequestJSON(new_photo_json);
        if ((isNaN(Number(new_photo_json.longitude))
            || isNaN(Number(new_photo_json.latitude)) || !json_valid)) {
            var error_json = { "status": 400,
                "body": { "error": "Request JSON invalid." } }
            return done(error_json);
        }

        new_photo_json.longitude = Number(new_photo_json.longitude);
        new_photo_json.latitude = Number(new_photo_json.latitude);

        // Generate buffers from images sent
        var image_buffer = new Buffer(new_photo_json.image, 'base64');
        var image_thumbnail_buffer =
                new Buffer(new_photo_json.image_thumbnail, 'base64');

        // Calculate image type
        var image_buffer_type = imageType(image_buffer)
        var image_thumbnail_buffer_type = imageType(image_thumbnail_buffer)

        // Either aren't an image, fuck them
        if (image_buffer_type == null || image_thumbnail_buffer_type == null) {
            var error_json = { "status": 400,
                "body": { "error": "Image or Thumbnail string is not an image"}}
            return done(error_json)
        }

        // Image uuid
        var image_uuid = uuid.v4()

        // Image path/url
        var image_file_name = image_uuid + '.' + image_buffer_type.ext
        var image_path = path.join(__dirname, '..', '..', 'images',
                image_file_name)
        var image_url = config.server.image_server + image_file_name

        // Image thumbnail path/url
        var image_thumbnail_file_name = image_uuid + '.thumb.' +
                image_thumbnail_buffer_type.ext
        var image_thumbnail_path = path.join(__dirname, '..', '..', 'images',
                image_thumbnail_file_name)
        var image_thumbnail_url = config.server.image_server +
                image_thumbnail_file_name

        // Write image to file
        fs.writeFile(image_path, new_photo_json.image, 'base64', function(err) {
            console.log("IMAGE:" + image_path);
        });

        // Write thumbnail image to file
        fs.writeFile(image_thumbnail_path, image_thumbnail_buffer,
                'base64', function(err) {
            console.log("THUMB:" + image_thumbnail_path);
        });

        // Create Photo
        db.Photos.create({
            user_id: auth_user_id,
            url: image_url,
            url_thumbnail: image_thumbnail_url,
            caption: new_photo_json.caption,
            longitude: new_photo_json.longitude,
            latitude: new_photo_json.latitude
        },{
        }).then(function(result) {
            var photo_object = result.dataValues;
            
            // Get User Info
            db.Users.findById(photo_object.user_id).then(function(result) {
                var photo_owner_object = result.dataValues;

                var timestamp = new Date(photo_object.created_at).getTime();

                var photo_json = {
                    "uploaded": true,
                    "photo_id": photo_object.id,
                    "photo_image": photo_object.url,
                    "photo_caption": photo_object.caption,
                    "timestamp": timestamp,
                    "user": {
                        "user_id": photo_owner_object.id,
                        "username": photo_owner_object.username,
                        "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                    },
                    "location": {
                        "longitude": photo_object.longitude,
                        "latitude": photo_object.latitude
                    }
                }

                return done(null, photo_json);
            });
        });
    },

    // GET comments
    getComments: function (photo_id, done) {

        db.Comments.findAll({
            include: [db.Users],
            where: {photo_id: photo_id}
        }).then(function(result) {

            var comments_json = { "comments": [] };

            if (result) {

                for (var i=0;i < result.length;i++) {

                    var comment_object = result[i].dataValues;
                    var comment_owner_object = result[i].User.dataValues;

                    var timestamp = new Date(comment_object.created_at).getTime();

                    var comment_json = {
                        "comment_id": comment_object.id,
                        "timestamp": timestamp,
                        "text": comment_object.text,
                        "from": {
                            "user_id": comment_owner_object.id,
                            "username": comment_owner_object.username,
                            "profile_image": "http://images.instamelb.pinkpineapple.me/1.jpg"
                        }
                    }

                    comments_json.comments.push(comment_json);
                }
            }

            return done(null, comments_json);

        });
    },

    // POST comment
    postComment: function (auth_user_id, photo_id, new_comment_json, done) {

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

        db.Comments.create({
            text: new_comment_json.text,
            user_id: auth_user_id,
            photo_id: photo_id
        }).then(function(result) {

            var post_comment_reponse = {
                "posted": true
            }

            return done(null, post_comment_reponse);

        });

    },

    // DELETE comment
    deleteComment: function (auth_user_id, photo_id, comment_id, done) {

        db.Comments.destroy({
            where: {
                id: comment_id,
                photo_id: photo_id,
                user_id: auth_user_id
            }
        }).then(function(result) {

            var delete_comment_response = {};

            if (result > 0) {
                // Something was deleted
                delete_comment_response.deleted = true;
            } else {
                delete_comment_response.deleted = false;
                delete_comment_response.error = "Comment not found.";
            } 

            return done(null, delete_comment_response);

        });
    },

    // GET likes
    getLikes: function (photo_id, done) {

        db.Likes.findAll({
            include: [db.Users],
            where: {photo_id: photo_id}
        }).then(function(result) {

            var likes_json = { "likes": [] };

            if (result) {

                for (var i=0;i < result.length;i++) {

                    var like_object = result[i].dataValues;
                    var like_owner_object = result[i].User.dataValues;

                    var timestamp = new Date(like_object.created_at).getTime();

                    var like_json = {
                        "user_id": like_owner_object.id,
                        "username": like_owner_object.username,
                        "timestamp": timestamp,
                    }
                    likes_json.likes.push(like_json);
                }
            }

            return done(null, likes_json);

        });
    },

    // POST likes
    postLike: function (auth_user_id, photo_id, done) {

        db.Likes.upsert({
            user_id: auth_user_id,
            photo_id: photo_id
        }, {}).then(function(result) {

            var like_photo_response = {};

            if (result) {
                like_photo_response.liked = true;
            } else {
                like_photo_response.liked = false;
                like_photo_response.error = "Already liked photo."
            }

            return done(null, like_photo_response);

        }).catch(function(error) {
            console.log(error)

            var error = {"status":404,"body":{"message":"Photo id does not exist."}}

            return done(error);
        });
    },

    // DELETE likes
    deleteLike: function (auth_user_id, photo_id, done) {

        db.Likes.destroy({
            where: {
                photo_id: photo_id,
                user_id: auth_user_id
            }
        }).then(function(result) {

            var delete_like_response = {};

            if (result > 0) {
                // Something was deleted
                delete_like_response.deleted = true;
            } else {
                delete_like_response.deleted = false;
                delete_like_response.error = "Photo not already liked.";
            } 

            return done(null, delete_like_response);

        });
    }

}}

