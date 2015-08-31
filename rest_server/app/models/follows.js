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
                    Photos.belongsTo(models.Users, { foreignKey: 'user_id' });
                }
            }
        }
    );
    return Follows;
}

