package com.application.keykeeper


class AccountCreate(_email: String, _password: String) {

    private val email: String
    private val passwordHash: String

    init {
        email = _email
        passwordHash = hashPassword(_password)
    }

    public fun sendCreateRequest() {
        // TODO: Send a HTTP request to the server with appropriate content.
        //println("Sending create request to server..")
    }

    private fun hashPassword(password: String): String {
        // TODO: Hash password using appropriate hashing method and email as salt.
        return "this_is_not_yet_hashed"
    }

    private fun generateSymkey(): String {
        // TODO: Generate a symkey. This should probably be handled by a different class.
        return "this_is_not_a_symkey"
    }

}