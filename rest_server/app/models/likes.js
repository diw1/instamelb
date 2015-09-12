// app/models/likes.js
// Likes database table definition

module.exports = function(sequelize, DataTypes) {

    var Likes = sequelize.define('Likes',
        {
        },
        {
            timestamps: true,
            tableName: 'Likes',
            classMethods: {
                associate: function(models) {
                    Likes.belongsTo(models.Users, { foreignKey: 'user_id' });
                    Likes.belongsTo(models.Photos, { foreignKey: 'photo_id' });
                }
            },
            indexes: [
                {
                    // User may like same photo only once
                    unique: true,
                    fields: ['user_id', 'photo_id']
                }
            ]
        }
    );
    return Likes;
}

