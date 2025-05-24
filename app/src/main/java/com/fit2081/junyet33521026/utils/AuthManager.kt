package com.fit2081.junyet33521026.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * AuthManager manages user authentication state
 * that provides functions to login and logout users and stores the current user ID.
 */
object AuthManager {
    // Mutable state to handle current user login
    private val _userId: MutableState<String?> = mutableStateOf(null)
    val currentUserId: String? get() = _userId.value

    /**
     * Checks if a user is logged in.
     * @return true or not a user is logged in
     */
    fun login(userId: String) {
        _userId.value = userId
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        _userId.value = null
    }

}