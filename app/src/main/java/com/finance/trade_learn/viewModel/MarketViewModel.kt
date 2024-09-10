package com.finance.trade_learn.viewModel

import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.FilterType
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.view.market.marketViewStates.MarketPageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MarketViewModel : BaseViewModel() {

    private val _searchBarViewState = MutableStateFlow<MarketPageUiState>(MarketPageUiState())
    val searchBarViewState : StateFlow<MarketPageUiState> get() = _searchBarViewState


    private val _itemList = MutableStateFlow<List<CoinsHome>>(currentItems)
    val itemList : StateFlow<List<CoinsHome>> get() = _itemList


    fun updateSearchBarViewState (searchBarViewState : MarketPageUiState){
        _searchBarViewState.value = searchBarViewState
        filterChanged()
    }

    private fun filterChanged() {
        val sortedList = when (searchBarViewState.value.filterType) {
            FilterType.Default -> currentItems
            FilterType.HighestPrice -> currentItems.sortedByDescending { it.CoinPrice.toDouble() }
            FilterType.LowestPrice -> currentItems.sortedBy { it.CoinPrice.toDouble() }
            FilterType.HighestPercentage -> currentItems.sortedByDescending { it.CoinChangePercente.toDouble() }
            FilterType.LowestPercentage -> currentItems.sortedBy { it.CoinChangePercente.toDouble() }
        }

        val filteredList = if (searchBarViewState.value.searchText.isEmpty()) sortedList
        else sortedList.filter {
            it.CoinName.contains(searchBarViewState.value.searchText, ignoreCase = true) ||
                    it.coinSymbol.contains(searchBarViewState.value.searchText, ignoreCase = true)
        }

        _itemList.value = filteredList
    }


}