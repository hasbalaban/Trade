package com.finance.trade_learn.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel.Companion.setLockMainActivityStatus
import com.finance.trade_learn.base.BaseViewModel.Companion.updateCurrencies
import com.finance.trade_learn.base.BaseViewModel.Companion.updateUserLoginStatus
import com.finance.trade_learn.base.BaseViewModel.Companion.updateUserWatchList
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.trade.BuySellScreenData
import kotlinx.coroutines.launch

data class CurrenciesUiState(
    val isLoading: Boolean = false,
    val data: BuySellScreenData = BuySellScreenData()
)


class CurrenciesViewModel : ViewModel() {

    init {
        getAllCurrencies()
    }


    private fun getAllCurrencies(){
        viewModelScope.launch {
            val userService = UserApi()
            val response = userService.getAllCurrencies()

            if (response.isSuccessful){
                response.body()?.data?.let {
                    updateCurrencies(it)
                }
                return@launch
            }

        }
    }
}