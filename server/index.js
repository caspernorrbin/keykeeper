
// Load .env file into process.env
require("dotenv").config();

const express = require("express");
const session = require("express-session");
const cookieParser = require("cookie-parser");
const app = express();

const PORT = process.env.PORT || 8080;

// Setup database connection
const sqlite3 = require("sqlite3").verbose();
const db = new sqlite3.Database(process.env.DB_LOC);

// Middlewares
const setDbMiddleWare = (req, _, next) => {
    req.db = db;

    next();
};

const needsLogin = (req, res, next) => { // what is this?

    if(req.session.loggedIn) {
        next();
    } else {
        // Redirect or send error message.
        res.status(403);
    }
};

// Setup middlewares
app.use(setDbMiddleWare);

// Setup cookies and session
app.use(cookieParser());
app.use(express.urlencoded({ extended: true }));

const HOUR = 60 * 60 * 1000;
app.use(session({
    httpOnly: false,
    resave: false,
    saveUninitialized: true,
    secret: process.env.SESSION_SECRET,
    cookie: { maxAge: 24 * HOUR } // 24 hour max session length.
}));

// Setup routes
app.get("/robots.txt", (_, res) => res.sendFile(__dirname + "/robots.txt"));
app.use("/api/sync", needsLogin, require("./routes/sync"));
app.use("/api/auth", require("./routes/auth"));
app.use("/api/account", require("./routes/account"));
app.use("/api/service", require("./routes/service"));

app.listen(PORT, _ => {
    console.log(`Listening on port ${PORT}`);
});