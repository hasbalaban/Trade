package com.finance.trade_learn.models

data class NewUserRequest(
    val email : String,
    val password : String,
    val userName : String,
    val userSurname : String,
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)