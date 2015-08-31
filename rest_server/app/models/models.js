// app/models/model.js
// Loads model files from app/models

var path = require('path')

// Models in db to be returned
var db = {}

module.exports = function(sequelize) {

	// Import models
	Users = sequelize["import"](path.join(__dirname, 'users.js'));
	Photos = sequelize["import"](path.join(__dirname, 'photos.js'));
	Follows = sequelize["import"](path.join(__dirname, 'follows.js'));
	Likes = sequelize["import"](path.join(__dirname, 'likes.js'));
	Comments = sequelize["import"](path.join(__dirname, 'comments.js'));

	// Stick models in db
	db['Users'] = Users;
	db['Photos'] = Photos;
    db['Follows'] = Follows;
    db['Likes'] = Likes;
    db['Comments'] = Comments;

	// Execute association function
	Object.keys(db).forEach(function(modelName) {
		// Check if has associate function
		if ("associate" in db[modelName]) {
			db[modelName].associate(db);
		}
	});

	// Export dbs
	return db;
}
