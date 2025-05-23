package com.fit2081.junyet33521026.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    private val _userId: MutableState<String?> = mutableStateOf(null)
    val currentUserId: String? get() = _userId.value

    fun login(userId: String) {
        _userId.value = userId
    }

    fun logout() {
        _userId.value = null
    }

}