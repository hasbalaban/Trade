package com.finance.trade_learn.viewModel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.FirebaseLogEvents
import com.finance.trade_learn.view.loginscreen.signup.SignUpViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _signUpViewState = MutableStateFlow<SignUpViewState>(SignUpViewState())
    val signUpViewState: StateFlow<SignUpViewState> get() = _signUpViewState


    private val _userSignUpResponse = MutableStateFlow<WrapResponse<UserInfo>>(WrapResponse())
    val userSignUpResponse: StateFlow<WrapResponse<UserInfo>> get() = _userSignUpResponse



    fun changeUserNameAndSurname(nameAndSurname: String) {
        _signUpViewState.value = _signUpViewState.value.copy(nameAndSurname = nameAndSurname)
    }

    fun changeEmail(email: String) {
        _signUpViewState.value = _signUpViewState.value.copy(email = email)
    }

    fun changePasswordText(password: String) {
        _signUpViewState.value = _signUpViewState.value.copy(password = password)
    }

    fun changeConfirmPasswordText(confirmPassword: String) {
        _signUpViewState.value = _signUpViewState.value.copy(confirmPassword = confirmPassword)
    }

    fun signUp() {
        if (!signUpViewState.value.credentialsIsValid) return

        _signUpViewState.value = _signUpViewState.value.copy(isLoading = true)
        BaseViewModel.setLockMainActivityStatus(shouldLockScreen = true)

        viewModelScope.launch {
            val userService = UserApi()
            val newUserRequest = NewUserRequest(
                email = signUpViewState.value.email,
                nameAndSurname = signUpViewState.value.nameAndSurname,
                password = signUpViewState.value.password
            )

            val response = userService.createNewUser(newUserRequest)

            BaseViewModel.setLockMainActivityStatus(shouldLockScreen = false)
            _signUpViewState.value = _signUpViewState.value.copy(isLoading = false)

            if (response.isSuccessful){
                response.body()?.let {
                    val bundle = Bundle()
                    bundle.putString("email", it.data?.email)
                    bundle.putString("email", it.data?.userId.toString())
                    bundle.putString("email", it.data?.totalBalance.toString())
                    bundle.putString("email", it.data?.nameAndSurname)
                    FirebaseLogEvents.logSignUpEvent(bundle)

                    _userSignUpResponse.value = it
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