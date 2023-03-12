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
     * Gets an account from the database
     * @param {string} id the id of the account
     * @returns {object} the account object if it exists, null otherwise
     */
    static getById(id) {
        let result;
        try {
            const getAccount = db.prepare("SELECT * FROM accounts WHERE id = ?");
            result = getAccount.get(id);
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
     * Updates an account in the database, ignores empty strings for parameters
     * @param {string} id the id of the account
     * @param {string} email the email of the account
     * @param {string} passwordHash the hashed password of the account, including salt
     * @param {string} symkey the encrypted symmetric key
     * @returns {boolean} true if the account was updated, false otherwise
     */
    static update(id, email, passwordHash, symkey) {
        let result;
        try {
            const updateAccount = db.prepare("UPDATE accounts SET email = IfNull(NullIf(?, ''), email), password_hash = IfNull(NullIf(?, ''), password_hash), symkey = IfNull(NullIf(?, ''), symkey) WHERE id = ?");
            result = updateAccount.run(email, passwordHash, symkey, id);
        } catch (err) {
            console.log(err);
            return false;
        }
        return true;
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