package com.finance.trade_learn.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
) : BaseViewModel() {


    private var disposable = CompositeDisposable()
    var isSuccess = MutableLiveData<Boolean>()

    val userBalance = MutableStateFlow<TradePageUiState<MyCoins?>>(TradePageUiState.Loading)

    private val _availableItemInfo = MutableStateFlow<TradePageUiState<MyCoins?>>(TradePageUiState.Loading)
    val availableItemInfo: StateFlow<TradePageUiState<MyCoins?>> = _availableItemInfo.asStateFlow()

    private val _itemCurrentInfo = MutableStateFlow<TradePageUiState<CoinDetail>>(TradePageUiState.Loading)
    val itemCurrentInfo: StateFlow<TradePageUiState<CoinDetail>> = _itemCurrentInfo.asStateFlow()



    fun getItemInfo (coinName: String): LiveData<MyCoins?> {
        return coinDetailRepositoryImp.getSelectedItemDetail(coinName)
    }

    fun setUserBalance(myCoins: MyCoins) {
        userBalance.value = TradePageUiState.Data(myCoins)
    }

    fun setDetailsOfCoinFromDatabase(myCoins: MyCoins) {
        _availableItemInfo.value = TradePageUiState.Data(myCoins)
    }

    fun getSelectedCoinDetails(coinName: String) {
        _availableItemInfo.value = TradePageUiState.Loading

        disposable.add(
            cryptoService().getSelectedCoinToTradeCoinGecko(coinName.lowercase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<List<CoinDetail>>() {

                    override fun onSuccess(it: List<CoinDetail>) {
                        it.firstOrNull()?.let {response ->
                            _itemCurrentInfo.value = TradePageUiState.Data(response)
                        }
                    }

                    override fun onError(e: Throwable) {
                        val cachedItem = cachedData.firstOrNull {
                            solveCoinName(it.id) == coinName
                        }
                        cachedItem?.let {
                            _itemCurrentInfo.value = TradePageUiState.Data(it)
                        }
                    }

                    }
                )
        )

    }


    fun operationTrade(
        itemAmount: Double,
        tradeType: TradeType
    ) {
        val item = when(val item = itemCurrentInfo.value){
            is TradePageUiState.Data -> item.data
            else -> return
        }
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
        }
    }




    // this function for buy coin that i want to be have
    private fun buyCoin(coinName: String, addCoinAmount: Double, total: Double, coinPrice: Double) {

        var userTotalBalance = when(val userInfo = userBalance.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

        val itemAmount = when(val userInfo = _availableItemInfo.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

        CoroutineScope(Dispatchers.IO).launch {

            val newAmount = itemAmount + addCoinAmount

            val myCoinItem = MyCoins(coinName.lowercase(Locale.getDefault()), newAmount)
            if (userTotalBalance >= total) {

                userTotalBalance -= total
                val myDollars = MyCoins("tether", userTotalBalance)

                if (coinName != "tether") {
                    withContext(Dispatchers.Main) {

                        try {

                            if (itemAmount > 0) coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
                            else coinDetailRepositoryImp.buyNewItem(myCoinItem)

                            coinDetailRepositoryImp.updateSelectedItem(myDollars)
                            isSuccess.value = true

                            saveTradeToDatabase(
                                coinName,
                                addCoinAmount,
                                coinPrice,
                                total,
                                TradeType.Buy
                            )
                        } catch (e: Exception) {
                            isSuccess.value = false
                        }

                    }

                }
            }

        }
    }


    // this function for sell coin that i have
    private fun sellCoin(coinName: String, sellAmount: Double, total: Double, coinPrice: Double) {

        var userTotalBalance = when(val userInfo = userBalance.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

        val itemAmount = when(val userInfo = _availableItemInfo.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

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
                        withContext(Dispatchers.Main) { isSuccess.value = true }
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
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

        Log.i("timetime", sdf.format(Date()))
        val currentTime = sdf.format(Date())

        val newTrade = SaveCoin(
            coinName = coinName,
            coinPrice = coinPrice.toBigDecimal().toString(),
            coinAmount = coinAmount.toBigDecimal().toString(),
            total = total.toBigDecimal().toString(),
            date = currentTime,
            tradeOperation = tradeOperation.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            coinDetailRepositoryImp.addProgressToTradeHistory(newTrade)
        }
    }


    fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) : Double{
        val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
        val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
        return amount.toDouble()
    }

    fun compare(amount: Double, tradeState: TradeType): Boolean {
        val balance = when(val userInfo = userBalance.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

        val availableAmount = when(val userInfo = availableItemInfo.value){
            is TradePageUiState.Data -> {
                userInfo.data?.CoinAmount ?: 0.0
            }
            else -> 0.0
        }

        val currentPrice = when(val userInfo = itemCurrentInfo.value){
            is TradePageUiState.Data -> {
                userInfo.data.current_price ?: 0.0
            }
            else -> 0.0
        }

        val totalCost = amount * currentPrice

        return try {
            when (tradeState) {
                TradeType.Buy -> {
                    ((balance >= totalCost) && (balance > 0.0) && (totalCost > 0.0) && (amount > 0.0))
                }
                TradeType.Sell -> {
                    ((availableAmount >= amount) && (availableAmount > 0.0) && (totalCost > 0.0) && (amount > 0.0))
                }
            }

        } catch (e: Exception) {
            false
        }
    }


    override fun onCleared() {
        disposable.clear()

        Log.i("clear", "clear")
        super.onCleared()
    }

}
