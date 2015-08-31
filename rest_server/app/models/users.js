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
                    Users.hasMany(models.Follows);
                    Users.hasMany(models.Likes);
                    Users.hasMany(models.Photos);
                    Users.hasMany(models.Comments);
                }
            }
        }
    );
    return Users;
}

