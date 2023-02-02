const sqlite3 = require('sqlite3');

const db = new sqlite3.Database(process.env.DB_LOC, (err) => {
    if (err) {
        throw err;
    }
});

module.exports = db;