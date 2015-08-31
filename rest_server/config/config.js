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
            name: 'instamelb',
            username: 'instamelb',
            password: 'password',
            host: 'localhost',
            port: 3306,
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
            name: 'instamelb',
            username: 'instamelb',
            password: 'password',
            host: 'localhost',
            port: 3306,
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
            name: 'instamelb',
            username: 'instamelb',
            password: 'password',
            host: 'localhost',
            port: 3306,
        },
        log: true,
        seed: true
    }
}
