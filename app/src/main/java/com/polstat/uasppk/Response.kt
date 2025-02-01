package com.polstat.uasppk

data class Response(
    val id: Long? = null,
    val openEndedAnswer: String?,
    val selectedChoices: List<String>?,
    val question: Question?,
    val user: User?
)