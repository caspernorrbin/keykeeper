const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const Account = require('../models/accountModel');

// create account request
router.post('/create', async (req, res) => {
    const { email, password, symkey } = req.body;
    // Check if email, password, or symkey is empty
    if (!email || !password || !symkey) { 
        return res.status(400).json({ message: "Empty email/password/symkey field" });
    }

    // Validate email format, not perfect but good enough for our use
    const emailRegex = /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/ig;
    if (!emailRegex.test(email)) {
        return res.status(400).json({ message: 'Invalid email format' });
    }

    // Check if account already exists
    const account = Account.get(email);
    if (account) {
        return res.status(400).json({ message: 'Account already exists' });
    }

    // Hash password, can increase salt rounds for increased security
    const saltRounds = 10;
    bcrypt.hash(password, saltRounds, (err, hash) => {
        if (err) {
            console.log(err);
            return res.status(400).json({ message: 'Account not created' });
        }
        
        // Create account
        const success = Account.create(email, hash, symkey);
        if (success) {
            return res.status(200).json({ message: 'Account created' });
        }
        return res.status(400).json({ message: 'Account not created' });
    });
});

router.post("/update", async (req, res) => {
    if (!req.session.loggedIn) {
        return res.status(403).send();
    }
    const id = req.session.accountId;

    const { oldpassword, email, password, symkey } = req.body;

    // Old password and new password is required
    if (!oldpassword || !password) {
        return res.status(400).json({ message: "Empty old password or new password field" });
    }

    if (email) {
        // Validate email format
        const emailRegex = /\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}\b/ig;
        if (!emailRegex.test(email)) {
            return res.status(400).json({ message: "Invalid email format" });
        }

        // Check if email is already in use
        const account = Account.get(email);
        if (account && account.id != id) {
            return res.status(400).json({ message: "Email already in use" });
        }
    }

    // Check if account exists
    const account = Account.getById(id);
    if (!account) {
        return res.status(400).json({ message: "Account not updated" });
    }

    // Check if old password is correct
    bcrypt.compare(oldpassword, account.password_hash, (err, result) => {
        if (err) {
            console.log(err);
            return res.status(400).json({ message: "Account not updated" });
        }
        if (result) {
            // Hash new password
            saltRounds = 10;
            bcrypt.hash(password, saltRounds, (err, hash) => {
                if (err) {
                    console.log(err);
                    return res.status(400).json({ message: "Account not updated" });
                }

                // Update account
                const success = Account.update(id, email, hash, symkey);
                if (success) {
                    return res.status(200).json({ message: "Account updated" });
                }
                return res.status(400).json({ message: "Account not updated" });
            });
        }
        else {
            return res.status(400).json({ message: "Incorrect password" });
        }
    });
});
    
// delete account request
router.post("/delete", async (req, res) => {
    const { email, password } = req.body;
    // Check if email or password is empty
    if (!email || !password) {
        return res.status(400).json({ message: "Empty email/password field" });
    }

    const account = Account.get(email);
    if (!account) {
        // Account does not exist
        return res.status(400).json({ message: "Email or password incorrect" });
    }

    // Compare password hashes
    bcrypt.compare(password, account.password_hash, (err, result) => {
        if (err) {
            console.log(err);
            return res.status(400).json({ message: "Account not deleted" });
        }

        if (result) {
            // Delete account
            const success = Account.delete(email);
            if (success) {
                // TODO: Delete all credentials associated with account

                return res.status(200).json({ message: "Account deleted" });
            }
            return res.status(400).json({ message: "Account not deleted" });
        }
        // Password incorrect
        return res.status(400).json({ message: "Email or password incorrect" });
    });

});

module.exports = router;