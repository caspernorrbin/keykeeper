# KeyKeeper

- Kotlin android application
- SQLite database
- NodeJS

## Application
- Initial setup by following this [guide](https://developer.android.com/codelabs/build-your-first-android-app-kotlin)
- Use android studio for development

## Database
- Initial empty SQLite database has ben created
- access using `sqlite3` command

## Server

1. Navigate into `server/`

2. Copy `.env-template` to `.env`, which should contain the following variables:

    * `SESSION_SECRET`: Secret used to store cookies on the client
    * `PORT`: The port that the server listens on
    * `DB_LOC`: The location of the SQLite database file the server uses

3. Run `npm install` to install dependencies

4. Run `npm run init_db` to create the database and its tables.

5. Run `npm start` to start the server or `npm run dev` to start the user in development mode, which reloads the server on file changes.