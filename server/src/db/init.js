require("dotenv").config();
const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database(process.env.DB_LOC);

db.serialize(() => {
    const accountsTable = `CREATE TABLE IF NOT EXISTS accounts(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email         TEXT NOT NULL UNIQUE,
            password_hash TEXT NOT NULL,
            symkey        TEXT NOT NULL);`;
    db.run(accountsTable, (err) => {
        if (err) {
            throw err;
        } else {
            console.log("Created table 'accounts'");
        }
    });
        
    const credentialsTable = `CREATE TABLE IF NOT EXISTS credentials(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            account_id INTEGER NOT NULL,
            service_name TEXT NOT NULL,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            uri TEXT NOT NULL,
            notes TEXT NOT NULL,
            FOREIGN KEY(account_id) REFERENCES accounts(id));`;
    db.run(credentialsTable, (err) => {
        if (err) {
            throw err;
        } else {
            console.log("Created table 'credentials'");
        }
    });
});

db.close();