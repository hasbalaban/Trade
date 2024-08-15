package com.finance.trade_learn.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.viewModel.SearchCoinViewModel


@Composable
fun SearchScreen(openTradePage: (String) -> Unit, viewModel: SearchCoinViewModel) {
    val baseViewModel = LocalBaseViewModel.current

    LaunchedEffect(Unit) {
        baseViewModel.getCoinList()
    }
}

fun filterList(filteredText: String, list : List<CoinsHome>) : List<CoinsHome> {
    if (filteredText.isEmpty()) return list

    val filteredList = list.filter {
        it.CoinName.contains(filteredText, ignoreCase = true) || it.coinSymbol.contains(filteredText, ignoreCase = true)
    }

    return filteredList
}
