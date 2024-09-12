package com.finance.trade_learn.view.loginscreen.login

import android.util.Patterns
import androidx.compose.ui.graphics.Color
import com.finance.trade_learn.view.loginscreen.signup.isValidEmail

data class LoginViewState(
    val email: String = "",
    //val email: String = "hasan-balaban@hotmail.com",
    val password: String = "",
    //val password: String = "123456",
    val isLoading: Boolean = false
){

    private val isEmailValid : Boolean
        get() = isValidEmail(email)

    private val isPasswordValid : Boolean
        get() = password.length > 5

    val credentialsIsValid : Boolean
        get() = isPasswordValid && isEmailValid



}
