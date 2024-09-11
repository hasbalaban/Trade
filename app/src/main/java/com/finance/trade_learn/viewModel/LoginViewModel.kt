package com.finance.trade_learn.viewModel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.UserInfo
import com.finance.trade_learn.models.UserLoginRequest
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.FirebaseLogEvents
import com.finance.trade_learn.view.loginscreen.login.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _loginViewState = MutableStateFlow<LoginViewState>(LoginViewState())
    val loginViewState: StateFlow<LoginViewState> get() = _loginViewState



    private val _userLoginResponse = MutableStateFlow<WrapResponse<UserInfo>>(WrapResponse())
    val userLoginResponse: StateFlow<WrapResponse<UserInfo>> get() = _userLoginResponse

    fun changeEmail(email: String) {
        _loginViewState.value = loginViewState.value.copy(email = email)
    }

    fun changePasswordText(password: String) {
        _loginViewState.value = loginViewState.value.copy(password = password)
    }

    fun login() {
        if (!loginViewState.value.credentialsIsValid) return

        _loginViewState.value = loginViewState.value.copy(isLoading = true)
        BaseViewModel.setLockMainActivityStatus(true)

        viewModelScope.launch {
            val userService = UserApi()
            val newUserRequest = UserLoginRequest(
                email = loginViewState.value.email,
                password = loginViewState.value.password,
            )

            val response = userService.login(newUserRequest)

            _loginViewState.value = loginViewState.value.copy(isLoading = false)
            BaseViewModel.setLockMainActivityStatus(false)

            if (response.isSuccessful){
                response.body()?.let {
                    val bundle = Bundle()
                    bundle.putString("email", it.data?.email)
                    bundle.putString("email", it.data?.userId.toString())
                    bundle.putString("email", it.data?.totalBalance.toString())
                    bundle.putString("email", it.data?.nameAndSurname)
                    FirebaseLogEvents.logLoginEvent(bundle)
                    _userLoginResponse.value = it
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }
            response.body()?.let {
                _userLoginResponse.value = it
            }

            println(response.message())
            println(response.body()?.message)
            println(response.body()?.error)
            println(response.body()?.success)


        }

    }

}