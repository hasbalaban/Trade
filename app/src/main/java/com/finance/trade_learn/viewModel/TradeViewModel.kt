package com.finance.trade_learn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.database.dataBaseEntities.UserTransactionsRequest
import com.finance.trade_learn.models.TradeType
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.service.user.UserApi
import com.finance.trade_learn.utils.solveCoinName
import com.finance.trade_learn.view.CoinProgress
import com.finance.trade_learn.view.trade.TradePageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
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

    fun buyCoin(amount: Double) {
        val item = itemCurrentInfo.value.data ?: return
        val currentPrice = item.current_price
        val coinName = item.id.lowercase(Locale.getDefault())

        if (currentPrice == null) return

        val total = amount * currentPrice
        val balance = userBalance.value?.CoinAmount ?: 0.0
        val isAvailableToBuy = ((balance >= total) && (balance > 0.0) && (total > 0.0) && (amount > 0.0))
        if (!isAvailableToBuy) return


        val itemAmount = _availableItemInfo.value.data?.CoinAmount ?: 0.0
        var userTotalBalance = userBalance.value?.CoinAmount ?: 0.0

        CoroutineScope(Dispatchers.IO).launch {
            val newAmount = itemAmount + amount

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
                                coinName, amount,
                                currentPrice, total
                            )
                        } else {
                            buyFromRemote(coinName, amount, currentPrice, total)
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
            email = userInfo.value.data?.email ?: "",
            transactionItemName = coinName,
            amount = addCoinAmount.toBigDecimal().toString(),
            price = coinPrice.toBigDecimal().toString(),
            transactionTotalPrice = total.toBigDecimal().toString(),
            transactionType = TradeType.Buy.toString(),
            date = System.currentTimeMillis().toString()
        )
        addTransactionHistory(transaction = transaction)
    }

    fun sellCoin(amount: Double) {
        val item = itemCurrentInfo.value.data ?: return
        val currentPrice = item.current_price
        val coinName = item.id.lowercase(Locale.getDefault())

        if (currentPrice == null) return

        var userTotalBalance = userBalance.value?.CoinAmount ?: 0.0
        val itemAmount = _availableItemInfo.value.data?.CoinAmount ?: 0.0

        val total = amount * currentPrice
        val isAvailableToBuy =   ((itemAmount >= amount) && (itemAmount > 0.0) && (total > 0.0) && (amount > 0.0))
        if (!isAvailableToBuy) return

        CoroutineScope(Dispatchers.IO).launch {

            if (itemAmount >= amount) {
                val newAmount = itemAmount - amount
                val myCoinItem = MyCoins(coinName, newAmount)

                userTotalBalance += total
                val myDollars = MyCoins("tether", userTotalBalance)

                if (coinName != "tether") {
                    if (!isLogin.value) {
                        sellFromLocal(
                            coinName,
                            amount,
                            currentPrice,
                            total,
                            myCoinItem,
                            myDollars
                        )
                    } else {
                        sellFromRemote(coinName, amount, currentPrice, total)
                    }
                }
                return@launch
            }
        }
    }


    private suspend fun sellFromLocal(
        coinName: String,
        amount: Double,
        currentPrice: Double,
        total: Double,
        myCoinItem: MyCoins,
        myDollars: MyCoins
    ) {
        try {
            coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
            coinDetailRepositoryImp.updateSelectedItem(myDollars)
            //and save to database, too
            saveTradeToDatabase(coinName, amount, currentPrice, total, TradeType.Sell)
        } catch (_: Exception) { }
    }


    private suspend fun sellFromRemote(coinName: String, amount : Double, coinPrice : Double, total: Double){
        val transaction = UserTransactionsRequest(
            email = "hasan-balaban@hotmail.com",
            transactionItemName = coinName,
            amount = amount.toString(),
            price = coinPrice.toString(),
            transactionTotalPrice = total.toString(),
            transactionType = TradeType.Sell.toString(),
            date = System.currentTimeMillis().toString()
        )
        addTransactionHistory(transaction = transaction)
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
        setLockMainActivityStatus(true)

        viewModelScope.launch {
            val userService = UserApi()
            val response = userService.addTransactionHistory(transaction = transaction)
            setLockMainActivityStatus(false)


            //_transactionViewState.value = transactionViewState.value.copy(isLoading = false)
            if (response.isSuccessful){
                response.body()?.data?.let {
                    updateUserBalance(it)
                    println(it)
                }
                println(response.body()?.success)
                return@launch
            }
            println(response.message())
        }
    }

    fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) : Double{
        val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
        val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
        return amount.toDouble()
    }
}
