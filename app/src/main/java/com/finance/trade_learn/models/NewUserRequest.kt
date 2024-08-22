package com.finance.trade_learn.models

data class NewUserRequest(
    val email : String,
    val password : String,
    val nameAndSurname : String,
)

data class User(
    val id: Int,
    val nameAndSurname: String,
    val email: String
)

data class UserLoginRequest(
    val email: String,
    val password: String
)
