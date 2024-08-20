package com.finance.trade_learn.models

data class ResetPasswordRequest (
    val email : String,
    val newPassword : String,
    val code : Int
)