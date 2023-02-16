package data

import structure.CredentialsItem
import kotlin.random.Random

var storageExampleItems = arrayListOf(
    CredentialsItem("Item A", "joxw", "secure"),
    CredentialsItem("Item B", "joel", "123"),
    CredentialsItem("Item C", "test", "abcd"),
    CredentialsItem("Item D", "jakob", "3210"),
    CredentialsItem("Item E", "nickname", "password"),
    CredentialsItem(createRandomPassword(), createRandomPassword(), "1234")
)

private fun createStorageItem(label: String, userName: String, password: String): CredentialsItem {
    return CredentialsItem(label, userName, password)
}

// creates a random password from 'characters'
private fun createRandomPassword(): String {
    val characters: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    //val characters = "abcdefghijklmnopqrstuvwxyz"
    var password = ""
    for (i in 0..31) {
        password += characters[Random.nextInt(characters.size)]
    }
    //val randPassword: String = List(5) { characters.random() }.joinToString { "" } // random password
    return password
//    return (1..32).map { characters.random() }.joinToString { "" }
    //return characters
}