package com.finance.trade_learn.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.viewModel.SearchCoinViewModel
import com.finance.trade_learn.viewModel.ViewModeHomePage
import java.util.*


@Composable
fun SearchScreen(openTradePage: (String) -> Unit, viewModel: SearchCoinViewModel) {
    val baseViewModel = LocalBaseViewModel.current

    LaunchedEffect(Unit) {
        baseViewModel.getCoinList()
    }
    SearchComposeView(openTradePage)
}

private fun getItemsList(searchedItems: String, viewModel: BaseViewModel): List<CoinsHome> {
    if (searchedItems.isEmpty()) return viewModel.currentItemsLiveData.value ?: emptyList()

    val filteredList = viewModel.currentItemsLiveData.value?.filter {
        it.CoinName.contains(searchedItems, ignoreCase = true) || it.coinSymbol.contains(searchedItems, ignoreCase = true)
    } ?: emptyList()

    return filteredList
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchComposeView(
    openTradePage: (String) -> Unit,
    viewModel: SearchCoinViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val baseViewModel = LocalBaseViewModel.current
    var searchedItem by remember { mutableStateOf("") }
    var resultItems by remember { mutableStateOf(getItemsList(searchedItem, viewModel = baseViewModel)) }

    val textChanged: (String) -> Unit = textChangedScope@{
        searchedItem = it
        resultItems = getItemsList(searchedItem, viewModel = baseViewModel)
    }

    Column(modifier = Modifier.fillMaxSize()) {

        TextField(
            modifier = Modifier
                .padding(top = 1.dp, start = 2.dp, end = 2.dp)
                .fillMaxWidth(),
            value = searchedItem, onValueChange = { value -> textChanged(value) },
            placeholder = {
                Text(text = stringResource(id = R.string.hintSearch))
            },
            maxLines = 1,
            singleLine = true
        )

        HomePageItems(coinsHome = resultItems) { selectedItemName ->
            openTradePage.invoke(selectedItemName)
        }

    }
}