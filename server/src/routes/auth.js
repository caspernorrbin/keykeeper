const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const Account = require('../models/accountModel');

// login request, creates a session if valid credentials are used in request.  
router.post("/login", (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: "Empty email/password field" });
    }

    // Get account from database
    const account = Account.get(email);
    if (!account) {
        // Account does not exist
        return res.status(400).json({ message: "Email or password incorrect" });
    }

    // Compare password hashes
    bcrypt.compare(password, account.password_hash, (err, result) => {
        if (err) {
            console.log(err);
            return res.status(400).json({ message: "Login failed" });
        }

        if (result) {
            // Set session variables
            req.session.accountId = account.id;
            req.session.loggedIn = true;

            console.log(account.symkey)
            return res.status(200).json(account.symkey); // (we want the symkey)
            // return res.status(200).json({message: "Logged in"})
        }
        // Password incorrect
        return res.status(400).json({ message: "Email or password incorrect" });
    });


});

router.post("/logout", (req, res) => {
    req.session.destroy();
    res.status(200).json({ message: "Logged out" });
});

module.exports = router;