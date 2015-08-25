// config.js
// Exportable JSON config object

module.exports = {

    development: {
        name: 'Dev. REST Server',
        server: {
            host: 'localhost',
            port: 3000
        },
        db: {
            host: 'localhost',
            port: 5984,
            table: {
            }
        },
        log: true,
        seed: false
    },

    test: {
        name: 'Test. REST Server',
        server: {
            host: 'localhost',
            port: 3000
        },
        db: {
            host: 'localhost',
            port: 5984,
            table: {
            }
        },
        log: false,
        seed: true
    },

    production: {
        name: 'Prod. REST Server',
        server: {
            host: 'localhost',
            port: 3000
        },
        db: {
            host: 'localhost',
            port: 5984,
            table: {
            }
        },
        log: true,
        seed: true
    }
}
