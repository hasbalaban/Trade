package com.finance.trade_learn.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.NewUserRequest
import com.finance.trade_learn.models.User
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.loginscreen.signup.SignUpViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _signUpViewState = MutableStateFlow<SignUpViewState>(SignUpViewState())
    val signUpViewState: StateFlow<SignUpViewState> get() = _signUpViewState


    private val _userSignUpResponse = MutableStateFlow<WrapResponse<User?>>(WrapResponse())
    val userSignUpResponse: StateFlow<WrapResponse<User?>> get() = _userSignUpResponse



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
        viewModelScope.launch {
            val userService = UserApi()
            val newUserRequest = NewUserRequest(
                email = signUpViewState.value.email,
                password = signUpViewState.value.password,
                userName = Random.nextInt().toString(),
                userSurname = Random.nextInt().toString(),
            )

            val response = userService.createNewUser(newUserRequest)

            _signUpViewState.value = _signUpViewState.value.copy(isLoading = false)
            if (response.isSuccessful){
                response.body()?.let {
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