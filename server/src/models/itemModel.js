const db = require('../db/db');

/**
 * @typedef DBItem
 * @property {string} id
 * @property {string} accountId the id of the user creating the item
 * @property {string} item_name the name of the item
 * @property {string} username the username part of the item
 * @property {string} password the encrypted password part of the item
 * @property {string} url the url part of the item
 * @property {string} notes the notes part of the item
 */

/**
 * Model for items (credentials / services), handles database operations
 */
class Item {
    /**
     * Creates an item in the database
     * @param {string} accountId the id of the user creating the item
     * @param {string} item_name the name of the item
     * @param {string} username the username part of the item
     * @param {string} password the encrypted password part of the item
     * @param {string} url the url part of the item
     * @param {string} notes the notes part of the item
     * @returns {boolean} true if the item was created, false otherwise
     */
    static create(accountId, item_name, username, password, uri = "", notes = "") {
        try {
            const createItem = db.prepare(`INSERT INTO items
                (account_id, item_name, username, password, uri, notes)
                VALUES(?, ?, ?, ?, ?, ?);
            `);
            createItem.run(accountId, item_name, username, password, uri, notes);
        } catch (err) {
            console.log(err);
            return false;
        }

        return true;
    }

    /**
     * Gets all items associated with an account from the database
     * @param {string} accountId the id of the user creating the item
     * @returns {DBItem[]} a list of items, null if error
     */
    static getAll(accountId) {
        let result;

        try {
            const getItems = db.prepare("SELECT * FROM items WHERE account_id = ?");
            result = getItems.all(accountId);
        } catch (err) {
            console.log(err);
            return null;
        }

        if (result === undefined) {
            return [];
        }
        
        return result;
    }

    /**
     * Deletes an account from the database
     * @param {string} accountId the id of the user creating the item
     * @param {string} itemId the id of the item to be deleted
     * @returns {boolean} true if the item was deleted, false otherwise
     */
    static delete(accountId, itemId) {
        let result;

        try {
            const deleteItem = db.prepare("DELETE FROM items WHERE account_id = ? AND id = ?");
            result = deleteItem.run(accountId, itemId);

            // If no rows were deleted, return false
            if(result.changes == 0) {
                return false;
            }
        } catch (err) {
            console.log(err);
            return false;
        }

        return result;
    }
}

module.exports = Item