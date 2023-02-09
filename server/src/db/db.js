const db = () => {
    const db = require('better-sqlite3')(process.env.DB_LOC);
    db.pragma('journal_mode = WAL');
    return db;
}

module.exports = db();