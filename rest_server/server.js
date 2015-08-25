// server.js
// Entry point
// Initializes app and starts up the server

var path = require('path')

// Initialize Config
var config = require(path.join(__dirname, 'config', 'config.js'))

// Initialize App (with Config)
app = require(path.join(__dirname, 'app.js'))(config.production)

// Start
app.start()

