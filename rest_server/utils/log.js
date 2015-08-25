// Log.js
// Cleaner logging

var util = require('util')

module.exports = function (config) {
    return function log (message) {
        if (config.log) {
            var timestamp = new Date().toTimeString();
            console.log("[" + timestamp  + "] [" + config.name + "] " + message);
        }
    }
}
