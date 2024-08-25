package com.finance.trade_learn.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.utils.solveCoinName
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseEntities.UserTransactionsRequest
import com.finance.trade_learn.models.TradeType
import com.finance.trade_learn.models.WrapResponse
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.view.CoinProgress
import com.finance.trade_learn.view.trade.TradePageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
) : BaseViewModel() {

    val userBalance = MutableStateFlow<MyCoins?>(MyCoins("", 0.0))

    private val _availableItemInfo = MutableStateFlow<TradePageUiState<MyCoins>>(TradePageUiState())
    val availableItemInfo: StateFlow<TradePageUiState<MyCoins>> get() =  _availableItemInfo

    private val _itemCurrentInfo = MutableStateFlow<TradePageUiState<CoinDetail>>(TradePageUiState(isLoading = false, data = null))
    val itemCurrentInfo: StateFlow<TradePageUiState<CoinDetail>> = _itemCurrentInfo.asStateFlow()



    fun getItemInfo (coinName: String): LiveData<MyCoins?> {
        return coinDetailRepositoryImp.getSelectedItemDetail(coinName)
    }

    fun setUserBalance(myCoins: MyCoins) {
        userBalance.value = myCoins
    }

    fun setDetailsOfCoinFromDatabase(myCoins: MyCoins) {
        _availableItemInfo.value = availableItemInfo.value.copy(data = myCoins)
    }

    fun getSelectedCoinDetails(coinName: String) {
        _itemCurrentInfo.value = itemCurrentInfo.value.copy(isLoading = true)

        val cachedItem = allCryptoItems.firstOrNull {
            solveCoinName(it.id) == coinName
        }
        cachedItem?.let {
            _itemCurrentInfo.value = itemCurrentInfo.value.copy(data = it)
        }

    }


    fun operationTrade(
        itemAmount: Double,
        tradeType: TradeType
    ) {
        val item = itemCurrentInfo.value.data ?: return
        val currentPrice = item.current_price

        when (tradeType) {
            TradeType.Buy -> {
                if (itemAmount.toString().isNotEmpty() && currentPrice != null) {
                    val total = itemAmount * currentPrice
                    buyCoin(item.name.lowercase(Locale.getDefault()), itemAmount, total, currentPrice)
                }
            }

            TradeType.Sell -> {
                if (itemAmount.toString() != "" && item.current_price != null) {
                    val total = itemAmount * item.current_price
                    sellCoin(item.name.lowercase(Locale.getDefault()), itemAmount, total, item.current_price)
                }
            }

            else -> {}
        }
    }




    // this function for buy coin that i want to be have
    private fun buyCoin(coinName: String, addCoinAmount: Double, total: Double, coinPrice: Double) {
        val itemAmount = _availableItemInfo.value.data?.CoinAmount ?: 0.0
        var userTotalBalance = userBalance.value?.CoinAmount ?: 0.0

        CoroutineScope(Dispatchers.IO).launch {
            val newAmount = itemAmount + addCoinAmount

            val myCoinItem = MyCoins(coinName.lowercase(Locale.getDefault()), newAmount)
            if (userTotalBalance >= total) {

                userTotalBalance -= total
                val myDollars = MyCoins("tether", userTotalBalance)

                if (coinName != "tether") {
                    withContext(Dispatchers.Main) {


                        if (!isLogin.value) {
                            buyFromLocal(
                                itemAmount,
                                myCoinItem, myDollars,
                                coinName, addCoinAmount,
                                coinPrice, total
                            )
                        } else {
                            buyFromRemote(coinName, addCoinAmount, coinPrice, total)
                        }
                    }

                }
            }

        }
    }


    private suspend fun buyFromLocal(
        itemAmount: Double,
        myCoinItem: MyCoins,
        myDollars: MyCoins,
        coinName: String,
        addCoinAmount: Double,
        coinPrice: Double,
        total: Double
    ) {

        if (itemAmount > 0) coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
        else coinDetailRepositoryImp.buyNewItem(myCoinItem)

        coinDetailRepositoryImp.updateSelectedItem(myDollars)

        saveTradeToDatabase(
            coinName,
            addCoinAmount,
            coinPrice,
            total,
            TradeType.Buy
        )
    }

    private suspend fun buyFromRemote(
        coinName: String,
        addCoinAmount: Double,
        coinPrice: Double,
        total: Double,
    ) {

        val transaction = UserTransactionsRequest(
            email = "hasan-balaban@hotmail.com",
            transactionItemName = coinName,
            amount = addCoinAmount.toBigDecimal().toString(),
            price = coinPrice.toBigDecimal().toString(),
            transactionTotalPrice = total.toBigDecimal().toString(),
            transactionType = TradeType.Sell.toString(),
            date = System.currentTimeMillis().toString()
        )


        addTransactionHistory(transaction = transaction)
    }


    // this function for sell coin that i have
    private fun sellCoin(coinName: String, sellAmount: Double, total: Double, coinPrice: Double) {
        var userTotalBalance = userBalance.value?.CoinAmount ?: 0.0
        val itemAmount = _availableItemInfo.value.data?.CoinAmount ?: 0.0

        CoroutineScope(Dispatchers.IO).launch {

            if (itemAmount >= sellAmount) {


                val newAmount = itemAmount - sellAmount
                val myCoinItem = MyCoins(coinName, newAmount)

                userTotalBalance += total
                val myDollars = MyCoins("tether", userTotalBalance)

                if (coinName != "tether") {

                    try {
                        coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
                        coinDetailRepositoryImp.updateSelectedItem(myDollars)
                        //and save to database, too
                        saveTradeToDatabase(coinName, sellAmount, coinPrice, total, TradeType.Sell)
                    } catch (_: Exception) {
                    }
                }
                return@launch

            }
        }
    }


    private fun saveTradeToDatabase(
        coinName: String,
        coinAmount: Double,
        coinPrice: Double,
        total: Double,
        tradeOperation: TradeType
    ) {
        val transaction = UserTransactions(
            transactionItemName = coinName,
            price = coinPrice.toBigDecimal().toString(),
            amount = coinAmount.toBigDecimal().toString(),
            transactionTotalPrice = total.toBigDecimal().toString(),
            date = System.currentTimeMillis().toString(),
            transactionType = tradeOperation.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            coinDetailRepositoryImp.addProgressToTradeHistory(transaction)
        }
    }

    private suspend fun addTransactionHistory(transaction: UserTransactionsRequest){
        //_transactionViewState.value = transactionViewState.value.copy(isLoading = true)

        viewModelScope.launch {
            val userService = UserApi()
            val response = userService.addTransactionHistory(transaction = transaction)

            //_transactionViewState.value = transactionViewState.value.copy(isLoading = false)
            if (response.isSuccessful){
                response.body()?.let {
                   // _transactionHistoryResponse.value = it
                    updateUserInfo(it)
                    println(it)
                    println(it)
                    println(it)
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


    fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) : Double{
        val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
        val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
        return amount.toDouble()
    }

    fun compare(amount: Double, tradeState: TradeType): Boolean {
        val balance = userBalance.value?.CoinAmount ?: 0.0
        val availableAmount = availableItemInfo.value.data?.CoinAmount ?: 0.0
        val currentPrice = itemCurrentInfo.value.data?.current_price ?: 0.0

        val totalCost = amount * currentPrice

        return try {
            when (tradeState) {
                TradeType.Buy -> {
                    ((balance >= totalCost) && (balance > 0.0) && (totalCost > 0.0) && (amount > 0.0))
                }
                TradeType.Sell -> {
                    ((availableAmount >= amount) && (availableAmount > 0.0) && (totalCost > 0.0) && (amount > 0.0))
                }

                else -> {false}
            }

        } catch (e: Exception) {
            false
        }
    }

}
