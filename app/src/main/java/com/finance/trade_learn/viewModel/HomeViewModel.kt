package com.finance.trade_learn.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.utils.solveCoinName
import com.finance.trade_learn.view.wallet.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
): ViewModel() {

    private val myCoinsDatabaseModel = MutableLiveData<List<MyCoins>>()
    val myCoinsNewModel = MutableLiveData<List<NewModelForItemHistory>>()

    private val _totalBalance = MutableStateFlow<Float>(0.0f)
    val totalBalance : StateFlow<Float> get() = _totalBalance

    fun getMyCoinsDetails() {
        CoroutineScope(Dispatchers.Main).launch {
            myCoinsDatabaseModel.value = coinDetailRepositoryImp.getAllItems()
            checkDatabaseData(myCoinsDatabaseModel)
        }
    }

    private fun checkDatabaseData(myCoinsDatabaseModel: MutableLiveData<List<MyCoins>>) {
        myCoinsDatabaseModel.value?.let {

            val idList = it.map { it.CoinName.lowercase() }
            getDataFromApi(idList)
        }
    }


    fun getDataFromApi(coinQuery: List<String>?) {
        if (coinQuery.isNullOrEmpty()) return
        val availableCoins = BaseViewModel.allCryptoItems.value
            .asSequence()
            .filter {item ->

                val id = solveCoinName(item.id)
                coinQuery.any {
                    it.lowercase(Locale.getDefault()) == id.lowercase(Locale.getDefault())
                            || it.lowercase(Locale.getDefault()) == item.name.lowercase(Locale.getDefault())
                            || it.lowercase(Locale.getDefault()) == item.symbol.lowercase(Locale.getDefault())
                }
            }.map {item ->
                val price = item.current_price?.toBigDecimal() ?: BigDecimal.ZERO

                val amount = if (BaseViewModel.isLogin.value)
                    BaseViewModel.userInfo.value.data?.balances?.firstOrNull {
                        it.itemName.lowercase(Locale.getDefault()) == item.id.lowercase(Locale.getDefault())
                                || it.itemName.lowercase(Locale.getDefault()) == item.name.lowercase(Locale.getDefault())
                                || it.itemName.lowercase(Locale.getDefault()) == item.symbol.lowercase(Locale.getDefault())
                    }?.amount?.toBigDecimal() ?: BigDecimal.ZERO
                else myCoinsDatabaseModel.value?.firstOrNull {
                    it.CoinName.lowercase(Locale.getDefault()) == item.id.lowercase(Locale.getDefault())
                            || it.CoinName.lowercase(Locale.getDefault()) == item.name.lowercase(Locale.getDefault())
                            || it.CoinName.lowercase(Locale.getDefault()) == item.symbol.lowercase(Locale.getDefault())
                }?.CoinAmount?.toBigDecimal() ?: BigDecimal.ZERO

                val totalItemBalance = amount * price

                NewModelForItemHistory(
                    CoinName =item.id.lowercase(Locale.getDefault()),
                    CoinAmount = amount.toDouble().format(6).toDouble(),
                    Total = totalItemBalance,
                    Image = item.image,
                    currentPrice = item.current_price.toString()
                )
            }.sortedByDescending { it.Total }
            .toList()

        viewModelScope.launch {
            myCoinsNewModel.value = availableCoins

            if (!BaseViewModel.isLogin.value){
                val total = availableCoins.sumOf { it.Total }.toFloat()
                _totalBalance.value = total
                return@launch
            }

            BaseViewModel.userInfo.value.data?.totalBalance?.let {
                _totalBalance.value = it.toFloat()
            }
        }

    }
}