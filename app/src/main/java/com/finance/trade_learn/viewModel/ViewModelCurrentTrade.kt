package com.finance.trade_learn.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.R
import com.finance.trade_learn.ctryptoApi.cryptoService
import com.finance.trade_learn.database.dataBaseEntities.myCoins
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.database.dataBaseService
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.models.SelectedPercent
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class ViewModelCurrentTrade(context: Context) : ViewModel() {
    private var disposable = CompositeDisposable()
    var state = MutableLiveData<Boolean>()
    val coinAmountLiveData = MutableLiveData<BigDecimal>()
    private val dao = dataBaseService.invoke(context).databaseDao()
    val selectedCoinToTradeDetails = MutableLiveData<List<CoinDetail>>()
    val canChangeAmount = MutableLiveData<Boolean>(true)
    private val _tradeType = MutableLiveData<TradeType>(TradeType.Buy)
    val tradeType : LiveData<TradeType> = _tradeType
    private val _selectedPercent =  MutableLiveData<SelectedPercent>(SelectedPercent.Percent25)
    val selectedPercent : LiveData<SelectedPercent> = _selectedPercent
    val buy = R.color.onClickBuyBack
    val sell = R.color.onClickSellBack


    fun changeTradeType (type : TradeType){
        _tradeType.value = type
    }

    fun changeSelectedPercent (percent : SelectedPercent){
        _selectedPercent.value = percent
    }

    // get details coin if exists in database - so if i have
    fun getDetailsOfCoinFromDatabase(coinName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val coin = dao.getOnCoinForTrade(coinName)
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

                    override fun onError(e: Throwable) {
                    }


                })
        )

    }


    // this function for buy coin that i want to be have
    fun buyCoin(coinName: String, addCoinAmount: Double, total: Double, coinPrice: Double) {
        val tradeOperation = TradeType.Buy

        CoroutineScope(Dispatchers.IO).launch {
            val myCoin = dao.getOnCoinForTrade(coinName)
            var myMoneyTotal = dao.getOneCoin("TETHER").CoinAmount

            if (myCoin != null) {
                val firstAmount = myCoin.CoinAmount
                val newAmount = firstAmount + addCoinAmount

                val myCoinItem = myCoins(coinName, newAmount)
                if (myMoneyTotal >= total) {

                    myMoneyTotal -= total
                    val myDollars = myCoins("TETHER", myMoneyTotal)

                    if (coinName != "TETHER") {
                        withContext(Dispatchers.Main) {

                            try {

                                dao.updateCoin(myCoinItem)
                                dao.updateCoin(myDollars)
                                state.value = true

                                saveTradeToDatabase(
                                    coinName,
                                    addCoinAmount,
                                    coinPrice,
                                    total,
                                    tradeOperation
                                )
                            } catch (e: Exception) {
                                state.value = false
                            }

                        }

                    }
                } else {
                    Log.i("islem", "the progres is failed")
                }

            } else {
                val myCoinItem = myCoins(coinName, addCoinAmount)

                if (myMoneyTotal >= total) {

                    myMoneyTotal -= total
                    val myDollars = myCoins("TETHER", myMoneyTotal)
                    if (coinName != "TETHER") {

                        withContext(Dispatchers.Main) {

                            try {

                                dao.addCoin(myCoinItem)// add new coin if doesn't exists
                                dao.updateCoin(myDollars) // update my dollars amaount
                                state.value = true
                                saveTradeToDatabase(
                                    coinName,
                                    addCoinAmount,
                                    coinPrice,
                                    total,
                                    tradeOperation
                                )

                            } catch (e: Exception) {
                                state.value = false
                            }

                        }

                    }
                } else {
                    Log.i("islem", "the progres is failed")
                }


            }


        }
    }


    // this function for sell coin that i have
    fun sellCoin(coinName: String, sellAmount: Double, total: Double, coinPrice: Double) {
        state.value = true
        val tradeOperation = TradeType.Sell

        CoroutineScope(Dispatchers.IO).launch {
            val myCoin = dao.getOneCoin(coinName)
            var myMoneyTotal = dao.getOneCoin("TETHER").CoinAmount


            val firstAmount = myCoin.CoinAmount
            if (firstAmount >= sellAmount) {


                val newAmount = firstAmount - sellAmount
                val myCoinItem = myCoins(coinName, newAmount)

                myMoneyTotal += total
                val myDollars = myCoins("TETHER", myMoneyTotal)

                if (coinName != "TETHER") {
                    withContext(Dispatchers.Main) {
                    }

                    try {

                        dao.updateCoin(myCoinItem)
                        dao.updateCoin(myDollars)
                        //and save to database, too
                        saveTradeToDatabase(coinName, sellAmount, coinPrice, total, tradeOperation)

                        withContext(Dispatchers.Main) {
                            state.value = true
                        }
                    } catch (_: Exception) { }
                }

            } else {
                state.value = false
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
            dao.addTrade(newTrade)
        }
    }

    override fun onCleared() {
        disposable.clear()

        Log.i("clear", "clear")
        super.onCleared()
    }

}
