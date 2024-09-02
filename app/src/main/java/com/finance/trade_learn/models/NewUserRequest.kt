package com.finance.trade_learn.models

data class NewUserRequest(
    val email : String,
    val password : String,
    val nameAndSurname : String,
)


data class UserInfo(
    val userId : Int,
    val email: String,
    val nameAndSurname: String,
    val balances: List<UserBalance>,
    val totalBalance: Double
)

data class UserBalance(
    val itemName: String,
    val amount: Double
)


data class UserLoginRequest(
    val email: String,
    val password: String
)



