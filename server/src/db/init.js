require("dotenv").config();
const db = require("better-sqlite3")(process.env.DB_LOC);
db.pragma("journal_mode = WAL");

const accountsTable = db.prepare(`CREATE TABLE IF NOT EXISTS accounts(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    symkey        TEXT NOT NULL);`);

accountsTable.run();
console.log("Created table 'accounts'");
        
const credentialsTable = db.prepare(`CREATE TABLE IF NOT EXISTS credentials(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    service_name TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    uri TEXT NOT NULL,
    notes TEXT NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(id));`);

credentialsTable.run();
console.log("Created table 'credentials'");

db.close();