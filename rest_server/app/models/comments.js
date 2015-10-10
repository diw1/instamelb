// app/models/comments.js
// Comments database table definition

module.exports = function(sequelize, DataTypes) {

    var Comments =  sequelize.define('Comments',
        {
            text: DataTypes.STRING,
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

