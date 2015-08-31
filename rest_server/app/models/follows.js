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
                    Follows.belongsTo(models.Users, { foreignKey: 'user_id' });
                    Follows.belongsTo(models.Users, { foreignKey: 'follow_user_id' });
                }
            }
        }
    );
    return Follows;
}

