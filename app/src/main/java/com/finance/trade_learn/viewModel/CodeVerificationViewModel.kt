package com.finance.trade_learn.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.ResetPasswordRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.loginscreen.codeverification.CodeVerificationViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CodeVerificationViewModel () : ViewModel() {

    private val _verificationViewState = MutableStateFlow<CodeVerificationViewState>(CodeVerificationViewState())
    val verificationViewState: StateFlow<CodeVerificationViewState> get() = _verificationViewState

    private val _verificationCodeResponse = MutableStateFlow<WrapResponse<String>>(WrapResponse())
    val verificationCodeResponse: StateFlow<WrapResponse<String>> get() = _verificationCodeResponse

    fun changeVerificationCodeText(code: String) {
        _verificationViewState.value = verificationViewState.value.copy(verificationCode = code)
    }

    fun changeEmail(email: String) {
        _verificationViewState.value = verificationViewState.value.copy(email = email)
    }

    fun changePasswordText(password: String) {
        _verificationViewState.value = verificationViewState.value.copy(password = password)
    }

    fun changeConfirmPasswordText(confirmPassword: String) {
        _verificationViewState.value = verificationViewState.value.copy(confirmPassword = confirmPassword)
    }


    fun onVerifyCode(){
        if (!verificationViewState.value.credentialsIsValid) return

        if (verificationViewState.value.verificationCode.toIntOrNull() == null) return

        BaseViewModel.setLockMainActivityStatus(true)
        _verificationViewState.value = verificationViewState.value.copy(isLoading = true)

        viewModelScope.launch {
            val userService = UserApi()

            val resetPasswordRequest = ResetPasswordRequest(
                email = verificationViewState.value.email,
                newPassword = verificationViewState.value.password,
                code = verificationViewState.value.verificationCode.toInt() ,
            )

            val response = userService.resetPassword(resetPasswordRequest = resetPasswordRequest)

            BaseViewModel.setLockMainActivityStatus(false)
            _verificationViewState.value = verificationViewState.value.copy(isLoading = false)

            if (response.isSuccessful){
                response.body()?.let {
                    _verificationCodeResponse.value = it
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