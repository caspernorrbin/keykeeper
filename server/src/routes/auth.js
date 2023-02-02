const express = require('express')

const router = express.Router();

// login request, creates a session if valid credentials are used in request.  
router.post("/login", (req, res) => {
    let { password } = req.body;

    // Check if password is empty.
    if (!password) {
        res.sendStatus(400);
        return;
    }

    // TODO: Check password against database. (this password should already be hashed, but hash again)
    if (password == "pw") {
        // @ts-ignore
        res.session.loggedIn = true;
        // @ts-ignore
        res.session.id = "john.doe@gmail.com" // TODO: temporary value
        res.sendStatus(200);
    } else {
        res.sendStatus(400);
    }
});

module.exports = router;