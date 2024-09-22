package com.finance.trade_learn.view.loginscreen.codeverification

import androidx.compose.ui.graphics.Color
import com.finance.trade_learn.view.loginscreen.signup.PasswordStatus
import com.finance.trade_learn.view.loginscreen.signup.isValidEmail

data class CodeVerificationViewState(
    val email : String = "",
    val verificationCode : String = "",
    val password : String = "",
    val confirmPassword : String = "",
    val isLoading : Boolean = false
){

    private val isEmailValid : Boolean
        get() = isValidEmail(email)

    private val isPasswordMatches : Boolean
        get() = password == confirmPassword
    val credentialsIsValid : Boolean
        get() = passwordStatus == PasswordStatus.Success && isEmailValid

    private val passwordStatus : PasswordStatus
        get() =
            if (password.isBlank() || confirmPassword.isBlank()) PasswordStatus.Default
            else if (password.length > 5 && isPasswordMatches) PasswordStatus.Success
            else PasswordStatus.Error

    val passwordBorderColor = when(passwordStatus) {
        PasswordStatus.Success -> Color.Green
        else -> Color.Gray
    }

    val confirmPasswordBorderColor = when(passwordStatus){
        PasswordStatus.Success -> Color.Green
        PasswordStatus.Default -> Color.Gray
        PasswordStatus.Error -> Color.Red
    }

    val codeBorderColor: Color
        get() = when (verificationCode.length > 5) {
            true -> Color.Green
            false -> Color.Gray
        }
}