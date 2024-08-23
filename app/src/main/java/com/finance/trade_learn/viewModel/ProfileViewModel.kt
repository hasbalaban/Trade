package com.finance.trade_learn.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.DataStoreKeys
import com.finance.trade_learn.utils.readStringPreference
import com.finance.trade_learn.view.profile.ProfileViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profileViewState = MutableStateFlow<ProfileViewState>(ProfileViewState())
    val profileViewState: StateFlow<ProfileViewState> get() = _profileViewState


    private val _accountDeletingResponse = MutableStateFlow<WrapResponse<String>>(WrapResponse())
    val accountDeletingResponse: StateFlow<WrapResponse<String>> get() = _accountDeletingResponse



    fun getUserEmail (context : Context){
        viewModelScope.launch {
            context.readStringPreference(DataStoreKeys.StringKeys.email).collect{
                _profileViewState.value = profileViewState.value.copy(userEmail = it)
            }
        }
    }

    fun deleteAccount() {
        _profileViewState.value = _profileViewState.value.copy(isAccountDeleting = true)

        viewModelScope.launch {
            val userService = UserApi()
            val response = userService.deleteAccount(email = profileViewState.value.userEmail)

            _profileViewState.value = _profileViewState.value.copy(isAccountDeleting = false)
            if (response.isSuccessful){
                response.body()?.let {
                    _accountDeletingResponse.value = it
                }
                println(response.body()?.success)
                response.body()?.data
                return@launch
            }
            response.body()?.let {
                _accountDeletingResponse.value = it
            }

            println(response.message())
            println(response.body()?.message)
            println(response.body()?.error)
            println(response.body()?.success)


        }

    }
}