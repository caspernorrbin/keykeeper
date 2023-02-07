require("dotenv").config();
const db = require("better-sqlite3")(process.env.DB_LOC);
db.pragma("journal_mode = WAL");

const accountsTable = db.prepare(`CREATE TABLE IF NOT EXISTS accounts(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email         TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    symkey        TEXT NOT NULL);`);

try {
    accountsTable.run();
    console.log("Created table 'accounts'");
} catch (err) {
    console.log(err);
}
        
const credentialsTable = db.prepare(`CREATE TABLE IF NOT EXISTS credentials(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_id INTEGER NOT NULL,
    service_name TEXT NOT NULL,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    uri TEXT NOT NULL,
    notes TEXT NOT NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(id));`);

try {
    credentialsTable.run();
    console.log("Created table 'credentials'");
} catch (err) {
    console.log(err);
}

res = db.prepare("INSERT INTO accounts (email, password_hash, symkey) VALUES (?, ?, ?)").run("d", "e", "f");
res = db.prepare("SELECT * FROM accounts").all();
console.log(res);

db.close();