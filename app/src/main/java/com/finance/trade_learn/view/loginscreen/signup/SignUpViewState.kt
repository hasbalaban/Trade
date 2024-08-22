package com.finance.trade_learn.view.loginscreen.signup

import android.util.Patterns
import androidx.compose.ui.graphics.Color

data class SignUpViewState(
    val email: String = "",
    val nameAndSurname : String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false
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

    val emailBorderColor: Color
        get() = when (isEmailValid) {
            true -> Color.Green
            false -> Color.Gray
        }

    val nameAndSurnameBorder: Color
        get() = when (nameAndSurname.length >= 2) {
            true -> Color.Green
            false -> Color.Gray
        }



}

enum class PasswordStatus{
    Default, Success, Error
}



fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
