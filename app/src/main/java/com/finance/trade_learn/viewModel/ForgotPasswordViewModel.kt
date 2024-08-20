package com.finance.trade_learn.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.loginscreen.forgotpassword.ForgotPasswordViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    private val _forgotPasswordViewState = MutableStateFlow<ForgotPasswordViewState>(ForgotPasswordViewState())
    val forgotPasswordViewState: StateFlow<ForgotPasswordViewState> get() = _forgotPasswordViewState


    private val _sendCodeResponse = MutableStateFlow<WrapResponse<String>>(WrapResponse())
    val sendCodeResponse: StateFlow<WrapResponse<String>> get() = _sendCodeResponse

    fun changeEmailText(email: String) {
        _forgotPasswordViewState.value = forgotPasswordViewState.value.copy(email = email)
    }


    fun sendResetPasswordCode(){
        _forgotPasswordViewState.value = forgotPasswordViewState.value.copy(isLoading = true)
        viewModelScope.launch {
            val userService = UserApi()

            val response = userService.sendResetPasswordCode(forgotPasswordViewState.value.email)

            _forgotPasswordViewState.value = forgotPasswordViewState.value.copy(isLoading = false)
            if (response.isSuccessful){
                response.body()?.let {
                    _sendCodeResponse.value = it
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }

            println(response.message())
            println(response.body()?.message)
            println(response.body()?.error)
            println(response.body()?.success)


        }
    }
}