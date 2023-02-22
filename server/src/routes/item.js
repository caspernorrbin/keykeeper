const express = require('express');
const router = express.Router();

const Item = require('../models/itemModel');

// Create item request
router.post('/create', async (req, res) => {

    const { item_name, username, password, uri, notes } = req.body;

    // Check if item_name, username, or password is empty, uri and notes 
    // are not checked since they are allowed to be empty.
    if (!item_name || !username || !password) { 
        return res.status(400).json({ message: "Empty item_name/username/password field(s)" });
    }

    const success = Item.create(req.session.accountId, item_name, username, password, uri, notes);
    if (success) {
        return res.status(200).json({ message: "Item created" });
    }

    return res.status(400).json({ message: "Item not created" });
});

// Get item associated with the logged in user request
router.get('/get/:itemId', async (req, res) => {
    // Get item with id '1' using "http://localhost:8080/api/item/get/1"
    const { itemId } = req.params;
    const result = Item.get(req.session.accountId, itemId);
    if (result) {
        return res.status(200).json(result);
    }

    return res.status(500).json({ message: "Failed to get item" });
});

// Get all items associated with the logged in user request
router.get('/getAll', async (req, res) => {

    const result = Item.getAll(req.session.accountId);
    if (result) {
        return res.status(200).json(result);
    }

    return res.status(500).json({ message: "Failed to get items" });
});

router.post("/update", async (req, res) => {
    const { itemId, data } = req.body;

    // Check if itemId is empty
    if (!itemId) {
        return res.status(400).json({ message: "Empty itemId field" });
    }
    // Check if data is empty
    if (!data || Object.keys(data).length == 0) {
        return res.status(400).json({ message: "Empty data field" });
    }

    const success = Item.update(req.session.accountId, itemId, data);
    if (success) {
        return res.status(200).json({ message: "Item updated" });
    }

    return res.status(400).json({ message: "Item not updated" });
})

// Delete item request
router.post("/delete", async (req, res) => {

    const { itemId } = req.body;

    // Check if itemId is empty
    if (!itemId) {
        return res.status(400).json({ message: "Empty itemId field" });
    }

    const success = Item.delete(req.session.accountId, itemId);
    if (success) {
        return res.status(200).json({ message: "Item removed" });
    }

    return res.status(400).json({ message: "Item not removed" });
});

module.exports = router