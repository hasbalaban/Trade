package com.finance.trade_learn.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.view.loginscreen.signup.SignUpViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {
    private val _signUpViewState  = MutableStateFlow<SignUpViewState>(SignUpViewState())
    val signUpViewState : StateFlow<SignUpViewState> get() = _signUpViewState

    fun changeEmail(email : String) {
        _signUpViewState.value = _signUpViewState.value.copy(email = email)
    }

    fun changePasswordText(password : String) {
        _signUpViewState.value = _signUpViewState.value.copy(password = password)
    }

    fun changeConfirmPasswordText(confirmPassword : String) {
        _signUpViewState.value = _signUpViewState.value.copy(confirmPassword = confirmPassword)
    }

}