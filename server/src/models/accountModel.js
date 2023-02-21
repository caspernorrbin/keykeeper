const db = require('../db/db');

/**
 * Model for accounts, handles database operations
 */
class Account {
    /**
     * Creates an account in the database
     * @param {string} email the email of the account
     * @param {string} passwordhash the hashed password of the account, including salt
     * @param {string} symkey the encrypted symmetric key
     * @returns {boolean} true if the account was created, false otherwise
     */
    static create(email, passwordhash, symkey) {
        // Create account
        try {
            const createAccount = db.prepare("INSERT INTO accounts (email, password_hash, symkey) VALUES (?, ?, ?)");
            createAccount.run(email, passwordhash, symkey);
        } catch (err) {
            console.log(err);
            return false;
        }
        return true;
    }

    /**
     * Gets an account from the database
     * @param {string} email the email of the account
     * @returns {object} the account object if it exists, null otherwise
     */
    static get(email) {
        let result;
        try {
            const getAccount = db.prepare("SELECT * FROM accounts WHERE email = ?");
            result = getAccount.get(email);
        } catch (err) {
            console.log(err);
            return null;
        }
        if (result === undefined) {
            return null;
        }
        return result;
    }

    /**
     * Deletes an account from the database
     * @param {string} email the email of the account
     * @returns {boolean} true if the account was deleted, false otherwise
     */
    static delete(email) {
        let result;
        try {
            const deleteAccount = db.prepare("DELETE FROM accounts WHERE email = ?");
            result = deleteAccount.run(email);
        } catch (err) {
            console.log(err);
            return false;
        }
        return true;
    }
}

module.exports = Account