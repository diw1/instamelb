// app/models/users.js
// Users database table definition

module.exports = function(sequelize, DataTypes) {

    var Users = sequelize.define('Users',
        {
            username: { type: DataTypes.STRING, unique: true, allowNull: false},
            email: { type: DataTypes.STRING, unique: true, allowNull: false },
            password: { type: DataTypes.STRING, allowNull: false }
        },
        {
            timestamps: true,
            tableName: 'Users',
            classMethods: {
                associate: function(models) {
                    Users.hasMany(models.Follows, { foreignKey: 'user_id' });
                    Users.hasMany(models.Likes, { foreignKey: 'user_id' });
                    Users.hasMany(models.Photos, { foreignKey: 'user_id', as: 'photo_owner'});
                    Users.hasMany(models.Comments, { foreignKey: 'user_id' });
                }
            }
        }
    );
    return Users;
}

