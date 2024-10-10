package com.humberto.tasky.auth.domain

fun String.toInitials(): String {
    val words = this.split(" ")
        .filter { it.isNotEmpty() }

    return if (words.size == 1) {
        words.first().take(2).uppercase()
    } else {
        "${words.first().first().uppercase()}${words.last().first().uppercase()}"
    }
}