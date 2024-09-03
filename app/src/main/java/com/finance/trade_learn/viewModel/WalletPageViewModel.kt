package com.finance.trade_learn.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.utils.solveCoinName
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WalletPageViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
) : BaseViewModel() {
    private val myCoinsDatabaseModel = MutableLiveData<List<MyCoins>>()
    val myCoinsNewModel = MutableLiveData<List<NewModelForItemHistory>>()

    private val _totalBalance = MutableStateFlow<Float>(0.0f)
    val totalBalance : StateFlow<Float> get() = _totalBalance

    // this function fot get coins that i have
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
        val availableCoins = allCryptoItems.value
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

                val amount = if (isLogin.value)
                    userInfo.value.data?.balances?.firstOrNull {
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
                CoinAmount = amount.toDouble(),
                Total = totalItemBalance,
                Image = item.image
            )
        }.sortedByDescending { it.Total }
            .toList()

        viewModelScope.launch {
            myCoinsNewModel.value = availableCoins

            if (!isLogin.value){
                _totalBalance.value = availableCoins.sumOf { it.Total }.toFloat()
                return@launch
            }

            userInfo.value.data?.totalBalance?.let {
                _totalBalance.value = it.toFloat()
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
    }
}