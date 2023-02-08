const express = require('express');
const Account = require('../models/accountModel');
const router = express.Router();

// create account request
router.get('/create', async (req, res) => {
    const { email, password, symkey } = req.body;
    // check to throw error if empty email/password fields
    if (!email || !password) { 
        return res.status(400).json({ message: 'Email and password required' });
    }
    // TODO: validate email format
    if (false) {
        return res.status(400).json({ message: 'Invalid email format' });
    }
    try {
        const salt = await bcrypt.genSalt(10);
        const hash = await bcrypt.hash(password, salt);
        const account = await Account.create(email, hash, salt, symkey);
        const token = signToken(account._id);
        res.status(201).json({ account, token });
    } catch (err) {
        console.log(err);
        res.status(400).json({ message: 'Account not created' });
    }
});

// delete account request
router.route("/delete", async (req, res) => {
    const {email, password} = req.body;
    res.status(501).json({ message: "Not implemented" });
});

module.exports = router;