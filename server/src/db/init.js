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


const itemsTable = db.prepare(`CREATE TABLE IF NOT EXISTS items(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    item_name TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    uri TEXT NOT NULL,
    notes TEXT NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(id));`);

itemsTable.run();
console.log("Created table 'items'");

db.close();