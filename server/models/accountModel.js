//const db = require('../db')

class Account {
    async create(email, passwordhash, salt, symkey) {
        // Check if email already exists in database (duplicates == bad)
        sql1 = "SELECT * FROM accounts WHERE email = ?"
        //let result = createAccountSql.run(email, passwordHash);

        // Create account (store credentials in database)
        // if(result.bad) {
            // account already exists, email bad...
            //return bad
        // }

        // Else success!

        // Return account
        return "john.smith@gmail.com" // TODO: temp value
    }
}