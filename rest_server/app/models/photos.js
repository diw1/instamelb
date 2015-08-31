// app/models/photos.js
// Photos database table definition

module.exports = function(sequelize, DataTypes) {

    var Photos =  sequelize.define('Photos',
        {
            url: { type: DataTypes.STRING, allowNull: false },
            caption: DataTypes.STRING,
            longitude: DataTypes.DECIMAL(9, 6),
            latitude: DataTypes.DECIMAL(9, 6)
        },
        {
            timestamps: true,
            tableName: 'Photos',
            classMethods: {
                associate: function(models) {
                    Photos.belongsTo(models.Users, { foreignKey: 'user_id' });

                    Photos.hasMany(models.Comments, { foreignKey: 'photo_id' });
                    Photos.hasMany(models.Likes, { foreignKey: 'photo_id' });
                }
            }
        }
    );
    return Photos;
}

