// app/models/comments.js
// Comments database table definition

module.exports = function(sequelize, DataTypes) {

    var Comments =  sequelize.define('Comments',
        {
            text: DataTypes.STRING,
            longitude: DataTypes.DECIMAL(9, 6),
            latitude: DataTypes.DECIMAL(9, 6)
        },
        {
            timestamps: true,
            tableName: 'Comments',
            classMethods: {
                associate: function(models) {
                    Comments.belongsTo(models.Users,
                            { foreignKey: 'user_id' });
                    Comments.belongsTo(models.Photos,
                            { foreignKey: 'photo_id' });
                }
            }
        }
    );
    return Comments;
}

