package com.fit2081.junyet33521026.utils

import java.security.MessageDigest

//    *** NEW FEATURE ***    //
/**
 * SHAEncrypter hashed passwords using SHA-256 for database security.
 */
object SHAEncrypter {

    /**
     * Encrypt a password using SHA-256 algorithm.
     * @param password The password to be hashed.
     * @return The hashed password as a hexadecimal string.
     */
    fun hashPasswordSHA(password: String): String {
        // SHA-256 MessageDigest instance
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())

        // convert byte array to hex string
        return bytes.joinToString("") { "%02x".format(it) }
    }
}