package com.fit2081.junyet33521026.utils

import java.security.MessageDigest

/**
 * SHAEncrypter is a utility class for hashing passwords using SHA-256.
 * It provides a method to hash a password and return the hashed value as a hex string.
 */
object SHAEncrypter {

    // Hash the password using SHA-256
    fun hashPasswordSHA(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())

        // Convert byte array to hex string
        return bytes.joinToString("") { "%02x".format(it) }
    }
}