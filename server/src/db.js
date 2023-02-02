const sqlite3 = require('sqlite3')

const db = new sqlite3.Database(process.env.DB_LOC, (err) => {
    if (err) {
        throw err;
    } else {
        console.log("Connected to the database.");
        // Create the table if it doesn't exist
        sql = `CREATE TABLE IF NOT EXISTS accounts(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email         TEXT NOT NULL UNIQUE,
            password_hash TEXT NOT NULL,
            symkey        TEXT NOT NULL,
        );`;
        
        db.run(sql, (err) => {
            if (err) {
                throw err;
            } else {
                console.log("Created table 'accounts'");
            }
        });

        sql2 = `CREATE TABLE IF NOT EXISTS credentials(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            account_id INTEGER NOT NULL,
            service_name TEXT NOT NULL,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            uri TEXT NOT NULL,
            notes TEXT NOT NULL,
            FOREIGN KEY(account_id) REFERENCES accounts(id)
        );`;
    }
});

module.exports = db;