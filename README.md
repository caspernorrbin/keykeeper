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
- Start using: `npm start`
- Development start using: `npm run dev`

#### Environment variables

A `.env` file should be created in the `server/` folder containing the following data:
```
SESSION_SECRET=abcdefg
PORT=8080
```
