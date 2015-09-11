// app/models/follows.js
// Follows database table definition

module.exports = function(sequelize, DataTypes) {

    var Follows =  sequelize.define('Follows',
        {
        },
        {
            timestamps: true,
            tableName: 'Follows',
            classMethods: {
                associate: function(models) {
                    Follows.belongsTo(models.Users,
                            { foreignKey: 'user_id', as: 'user_following' });
                    Follows.belongsTo(models.Users,
                            { foreignKey: 'follow_user_id', as: 'user_followed' });
                }
            },
            indexes: [
                {
                    name: 'follower_followed_index',
                    unique: true,
                    fields: ['user_id', 'follow_user_id']
                }
            ]
        }
    );
    return Follows;
}

