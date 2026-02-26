package com.oliviermarteaux.a056_bricksbreaker.domain

data class User(
    val id: String,
    val email: String,
    val pseudo: String,
    val score: Long
)
