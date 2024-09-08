package com.finance.trade_learn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
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
import com.finance.trade_learn.view.trade.BuySellScreenData
import com.finance.trade_learn.view.trade.BuySellScreenUiState
import com.finance.trade_learn.view.trade.TradePageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TvViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
): ViewModel() {

    private val _tradePageUiState = MutableStateFlow<BuySellScreenUiState>(BuySellScreenUiState())
    val tradePageUiState: StateFlow<BuySellScreenUiState> get() =  _tradePageUiState


    private val _selectedItemDetail = MutableStateFlow<TradePageUiState<CoinDetail>>(TradePageUiState(isLoading = false, data = null))
    val selectedItemDetail: StateFlow<TradePageUiState<CoinDetail>> = _selectedItemDetail.asStateFlow()

    fun setSelectedCoinDetails(itemId: String) {
        val cachedItem = BaseViewModel.allCryptoItems.value.firstOrNull {
            solveCoinName(it.id) == itemId
        }
        cachedItem?.let {
            _selectedItemDetail.value = selectedItemDetail.value.copy(data = it)
        }

        initializeViewState()
    }

    private fun initializeViewState() {
        selectedItemDetail.value.data?.let {
            val data = tradePageUiState.value.data.copy(
                _currentPrice = it.current_price?: 0.0,
                _dailyPercentChange = it.price_change_percentage_24h ?: 0.0,
            )

            changeViewState(tradePageUiState.value.copy(data = data))
        }
    }


    fun changeViewState(buySellScreenUiState : BuySellScreenUiState){
        this._tradePageUiState.value = buySellScreenUiState
    }



    fun getItemInfo (): LiveData<List<MyCoins>> {
        return coinDetailRepositoryImp.getAllCoinsAsLiveData()
    }


    fun clickedBuy(){
        val tradeUiState = tradePageUiState.value.data ?: return
        val item = selectedItemDetail.value.data ?: return

        if (!tradeUiState.isBuyEnabled) return

        CoroutineScope(Dispatchers.IO).launch {

            if (tradeUiState.isLogin) {
                buyOrSellFromRemote(tradeUiState = tradeUiState, item = item, transactionType = TradeType.Buy)
                return@launch
            }

            buyFromLocal(tradeUiState = tradeUiState, item = item)
        }
    }



    fun clickedSell(){
        val tradeUiState = tradePageUiState.value.data ?: return
        val item = selectedItemDetail.value.data ?: return
        CoroutineScope(Dispatchers.IO).launch {

            if (tradeUiState.isLogin) {
                buyOrSellFromRemote(tradeUiState = tradeUiState, item = item, transactionType = TradeType.Sell)
                return@launch
            }

            sellFromLocal(tradeUiState = tradeUiState, item = item)
        }
    }


    private suspend fun buyFromLocal(tradeUiState: BuySellScreenData, item: CoinDetail) {
        val myCoinItem = MyCoins(
            CoinName = item.id.lowercase(Locale.getDefault()),
            CoinAmount = tradeUiState.ownedAmount + tradeUiState.transactionAmount
        )
        if (tradeUiState.ownedAmount > 0) coinDetailRepositoryImp.updateSelectedItem(myCoinItem)
        else coinDetailRepositoryImp.buyNewItem(myCoinItem)

        val newBalance = tradeUiState.balance - tradeUiState.totalTransactionCost
        val myDollars = MyCoins("tether", newBalance)
        coinDetailRepositoryImp.updateSelectedItem(myDollars)

        saveTradeToDatabase(
            tradeUiState = tradeUiState,
            item = item,
            TradeType.Buy
        )
    }

    private suspend fun sellFromLocal(
        tradeUiState: BuySellScreenData, item: CoinDetail
    ) {
        try {
            val myCoinItem = MyCoins(
                CoinName = item.id.lowercase(Locale.getDefault()),
                CoinAmount = tradeUiState.ownedAmount - tradeUiState.transactionAmount
            )
            coinDetailRepositoryImp.updateSelectedItem(myCoinItem)

            val newBalance = tradeUiState.balance + tradeUiState.totalTransactionCost
            val myDollars = MyCoins("tether", newBalance)
            coinDetailRepositoryImp.updateSelectedItem(myDollars)
            //and save to database, too
            saveTradeToDatabase(
                tradeUiState = tradeUiState,
                item = item,
                TradeType.Sell
            )
        } catch (_: Exception) { }
    }


    private suspend fun buyOrSellFromRemote(tradeUiState: BuySellScreenData, item: CoinDetail, transactionType : TradeType) {
        val transaction = UserTransactionsRequest(
            email = BaseViewModel.userInfo.value.data?.email ?: "",
            transactionItemName = item.id,
            amount = tradeUiState.transactionAmount.toString(),
            price = tradeUiState.currentPrice.toString(),
            transactionTotalPrice = tradeUiState.totalTransactionCost.toString(),
            transactionType = transactionType.toString(),
            date = System.currentTimeMillis().toString()
        )
        addTransactionHistory(transaction = transaction)
    }

    private suspend fun addTransactionHistory(transaction: UserTransactionsRequest){
        BaseViewModel.setLockMainActivityStatus(true)

        viewModelScope.launch {
            val userService = UserApi()
            val response = userService.addTransactionHistory(transaction = transaction)
            BaseViewModel.setLockMainActivityStatus(false)


            //_transactionViewState.value = transactionViewState.value.copy(isLoading = false)
            if (response.isSuccessful){
                response.body()?.data?.let {
                    BaseViewModel.updateUserBalance(it)
                    println(it)
                }
                println(response.body()?.success)
                return@launch
            }
            println(response.message())
        }
    }

    private suspend fun saveTradeToDatabase(
        tradeUiState: BuySellScreenData,
        item: CoinDetail,
        tradeOperation: TradeType
    ) {
        val transaction = UserTransactions(
            transactionItemName = item.id,
            price = tradeUiState.currentPrice.toString(),
            amount = tradeUiState.transactionAmount.toString(),
            transactionTotalPrice = tradeUiState.totalTransactionCost.toString(),
            date = System.currentTimeMillis().toString(),
            transactionType = tradeOperation.toString()
        )
        coinDetailRepositoryImp.addProgressToTradeHistory(transaction)
    }


}