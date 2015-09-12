// routes/photo.js
// Contains photo routes

// Utility Modules
var path = require('path');
var log = require(path.join(__dirname, '..', 'utils', 'log.js'));
var auth = require(path.join(__dirname, '..', 'utils', 'auth.js'));

// Controller
var photo_controller = require(path.join(__dirname, '..',
            'app', 'controllers', 'photo-controller.js'));
// View
var photo_view = require(path.join(__dirname, '..',
            'app', 'views', 'photo-view.js'));

module.exports = function (config, app, db, passport) {
    
    // Logger
    var logger = log(config);

    photo_controller = photo_controller(config, db);
    photo_view = photo_view(config);

    // Get Comments
    app.get('/photo/:photo_id/comments', auth(passport), function (req, res) {
        logger("GET /photo/:photo_id/comments");

        photo_controller.getComments(req.params.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.getComments(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Post Comment
    app.post('/photo/:photo_id/comments', auth(passport), function (req, res) {
        logger("POST /photo/:photo_id/comments");

        photo_controller.postComment(req.user.id, req.params.photo_id, req.body,
                function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.postComment(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Delete Comment
    app.delete('/photo/:photo_id/comments/:comment_id', function (req, res) {
        logger("DELETE /photo/:photo_id/comments/:comment_id");

        photo_controller.deleteComment(req.params.photo_id, req.params.comment_id,
                function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.deleteComment(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Get Likes
    app.get('/photo/:photo_id/likes', function (req, res) {
        logger("GET /photo/:photo_id/likes");

        photo_controller.getLikes(req.params.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.getLikes(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Like Photo
    app.post('/photo/:photo_id/likes', function (req, res) {
        logger("POST /photo/:photo_id/likes");

        photo_controller.postLike(req.params.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.postLike(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Delete Like
    app.delete('/photo/:photo_id/likes', function (req, res) {
        logger("DELETE /photo/:photo_id/likes");

        photo_controller.deleteLike(req.params.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.deleteLike(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Get Photo
    app.get('/photo/:photo_id', auth(passport), function (req, res) {
        logger("GET /photo/:photo_id");

        photo_controller.getPhoto(req.params.photo_id, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.getPhoto(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

    // Post Photo
    app.post('/photo', auth(passport), function (req, res) {
        logger("POST /photo");

        photo_controller.postPhoto(req.user.id, req.body, function done (error, result) {
            if (error) { return res.status(error.status).json(error.body); }

            photo_view.postPhoto(result, function done (error, result) {
                if (error) { return res.status(error.status).json(error.body); }
                // Response
                return res.status(200).json(result);
            });
        });
    });

}

