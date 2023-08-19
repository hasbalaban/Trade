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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.finance.trade_learn.R
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.viewModel.SearchCoinViewModel
import java.util.*


@Composable
fun SearchView(openTradePage : (String) -> Unit, viewModel: SearchCoinViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    viewModel.getCoinList()
    SetComposeView(openTradePage)
}

private fun getItemsList (searchedItems : String, viewModel: SearchCoinViewModel): List<CoinInfoList> {
    if (searchedItems.isEmpty()) return emptyList()

    val queryList = viewModel.coinListDetail.value?.filter {
        it.name.contains(searchedItems, ignoreCase = true)
    }
    return queryList ?: emptyList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetComposeView (openTradePage : (String) -> Unit, viewModel: SearchCoinViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
        var searchedItem by remember { mutableStateOf("") }
        var resultItems by remember { mutableStateOf(emptyList<CoinInfoList>()) }

        val textChanged : (String) -> Unit =  {
            searchedItem = it
            resultItems = getItemsList(searchedItem, viewModel = viewModel )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            TextField(
                modifier = Modifier
                    .padding(top = 1.dp, start = 2.dp, end = 2.dp)
                    .fillMaxWidth(),
                value = searchedItem, onValueChange = {value -> textChanged(value) },
                placeholder = {
                    Text(text = stringResource(id = R.string.hintSearch))
                },
                maxLines = 1,
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = resultItems,
                    key = {
                        it.coinId
                    }
                ) {
                    SearchItemComposeView(CoinInfo = it){itemId ->
                        openTradePage(itemId)
                    }
                }
            }

        }
}