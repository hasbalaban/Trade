package com.finance.trade_learn.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.utils.solveCoinName
import com.finance.trade_learn.view.trade.BuySellScreenData
import com.finance.trade_learn.view.trade.BuySellScreenUiState
import com.finance.trade_learn.view.trade.TradePageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TvViewModel @Inject constructor(
    private val coinDetailRepositoryImp : CoinDetailRepositoryImp
): ViewModel() {

    private val _tradePageUiState = MutableStateFlow<BuySellScreenUiState>(BuySellScreenUiState())
    val tradePageUiState: StateFlow<BuySellScreenUiState> get() =  _tradePageUiState


    private val _selectedItemDetail = MutableStateFlow<TradePageUiState<CoinDetail>>(TradePageUiState(isLoading = false, data = null))
    val selectedItemDetail: StateFlow<TradePageUiState<CoinDetail>> = _selectedItemDetail.asStateFlow()



    fun initializeViewState() {
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

    fun setSelectedCoinDetails(itemId: String) {
        val cachedItem = BaseViewModel.allCryptoItems.value.firstOrNull {
            solveCoinName(it.id) == itemId
        }
        cachedItem?.let {
            _selectedItemDetail.value = selectedItemDetail.value.copy(data = it)
        }

        initializeViewState()
    }

    fun getItemInfo (): LiveData<List<MyCoins>> {
        return coinDetailRepositoryImp.getAllCoinsAsLiveData()
    }

}