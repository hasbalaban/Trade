package com.finance.trade_learn.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.myCoins
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.models.SelectedPercent
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ViewModelCurrentTrade @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
) : BaseViewModel() {

    private var disposable = CompositeDisposable()
    var isSuccess = MutableLiveData<Boolean>()
    val coinAmountLiveData = MutableLiveData<BigDecimal?>()
    val selectedCoinToTradeDetails = MutableLiveData<List<CoinDetail>>()

    private val _tradeType = MutableLiveData<TradeType>(TradeType.Buy)
    val tradeType : LiveData<TradeType> = _tradeType

    val selectedPercent =  MutableLiveData<SelectedPercent>()

    fun changeTradeType (type : TradeType){
        _tradeType.value = type
    }

    fun changeSelectedPercent (percent : SelectedPercent){
        selectedPercent.value = percent
    }

    // get details coin if exists in database - so if i have
    fun getDetailsOfCoinFromDatabase(coinName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val coin = coinDetailRepositoryImp.getSelectedItemDetail(coinName)
            withContext(Dispatchers.Main) {
                coinAmountLiveData.value = BigDecimal.valueOf(0.0)
                if (coin != null) {
                    coinAmountLiveData.value = coin.CoinAmount.toBigDecimal()
                }

            }
        }
    }

    // this function for get details of coin that  i will buy
    fun getSelectedCoinDetails(coinName: String) {
        disposable.add(
            cryptoService().getSelectedCoinToTradeCoinGecko(coinName.lowercase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<List<CoinDetail>>() {

                    override fun onSuccess(t: List<CoinDetail>) {
                        selectedCoinToTradeDetails.value = t
                    }

                    override fun onError(e: Throwable) {}

                    }
                )
        )

    }


    // this function for buy coin that i want to be have
    fun buyCoin(coinName: String, addCoinAmount: Double, total: Double, coinPrice: Double) {
        val tradeOperation = TradeType.Buy

        CoroutineScope(Dispatchers.IO).launch {
            val myCoin = coinDetailRepositoryImp.getSelectedItemDetail(coinName)
            var myMoneyTotal = coinDetailRepositoryImp.getSelectedItemDetail("tether")?.CoinAmount ?: 0.0

            if (myCoin != null) {
                val firstAmount = myCoin.CoinAmount
                val newAmount = firstAmount + addCoinAmount

                val myCoinItem = myCoins(coinName.lowercase(Locale.getDefault()), newAmount)
                if (myMoneyTotal >= total) {

                    myMoneyTotal -= total
                    val myDollars = myCoins("tether", myMoneyTotal)

                    if (coinName != "tether") {
                        withContext(Dispatchers.Main) {

                            try {

                                coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
                                coinDetailRepositoryImp.updateSelectedItem(myDollars)
                                isSuccess.value = true

                                saveTradeToDatabase(
                                    coinName,
                                    addCoinAmount,
                                    coinPrice,
                                    total,
                                    tradeOperation
                                )
                            } catch (e: Exception) {
                                isSuccess.value = false
                            }

                        }

                    }
                }

            } else {
                val myCoinItem = myCoins(coinName, addCoinAmount)

                if (myMoneyTotal >= total) {
                    myMoneyTotal -= total
                    val myDollars = myCoins("tether", myMoneyTotal)
                    if (coinName == "TETHER"  || coinName == "tether") {
                        return@launch
                    }
                    withContext(Dispatchers.Main) {

                        try {
                            coinDetailRepositoryImp.buyNewItem(myCoinItem)// add new coin if doesn't exists
                            coinDetailRepositoryImp.updateSelectedItem(myDollars) // update my dollars amaount
                            isSuccess.value = true
                            saveTradeToDatabase(
                                coinName,
                                addCoinAmount,
                                coinPrice,
                                total,
                                tradeOperation
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
    fun sellCoin(coinName: String, sellAmount: Double, total: Double, coinPrice: Double) {
        val tradeOperation = TradeType.Sell

        CoroutineScope(Dispatchers.IO).launch {
            val myCoin = coinDetailRepositoryImp.getSelectedItemDetail(coinName)
            var myMoneyTotal = coinDetailRepositoryImp.getSelectedItemDetail("tether")?.CoinAmount ?: 0.0


            val firstAmount = myCoin?.CoinAmount ?: 0.0
            if (firstAmount >= sellAmount) {


                val newAmount = firstAmount - sellAmount
                val myCoinItem = myCoins(coinName, newAmount)

                myMoneyTotal += total
                val myDollars = myCoins("tether", myMoneyTotal)

                if (coinName != "tether") {

                    try {
                        coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
                        coinDetailRepositoryImp.updateSelectedItem(myDollars)
                        //and save to database, too
                        saveTradeToDatabase(coinName, sellAmount, coinPrice, total, tradeOperation)
                        withContext(Dispatchers.Main) { isSuccess.value = true }
                    } catch (_: Exception) { }
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

    override fun onCleared() {
        disposable.clear()

        Log.i("clear", "clear")
        super.onCleared()
    }

}
