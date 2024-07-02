package com.finance.trade_learn.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.R
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.view.wallet.WalletItemComposeView
import com.finance.trade_learn.viewModel.ViewModelMyWallet
import java.util.*

@Composable
fun WalletScreen(
    openTradePage: (String) -> Unit,
    viewModel: ViewModelMyWallet,
) {

    LaunchedEffect(key1 = Unit){
        viewModel.getMyCoinsDetails()
    }

    val myCoins = viewModel.myCoinsNewModel.observeAsState().value?.map { it }
    var searchedItem by remember { mutableStateOf("") }
    var resultItems by remember { mutableStateOf(emptyList<NewModelForItemHistory>()) }

    val textChanged : (String) -> Unit =textChangedScope@{
        searchedItem = it
        resultItems = getSearchedList(searchedItem, itemList = viewModel.myCoinsNewModel.value )
    }

    val totalValuePrice = viewModel.totalValue.observeAsState().value?.let {
        ("≈ " + (it.toString() + "000000000000")).subSequence(0, 10).toString()
    } ?: ""

    Column(modifier = Modifier.fillMaxSize()) {

        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                modifier = Modifier
                    .size(64.dp)
                    .padding(start = 12.dp, top = 6.dp),
                painter = painterResource(id = R.drawable.ust), contentDescription = null,
                contentScale = ContentScale.Inside
            )

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                text = stringResource(id = R.string.TotalValue),
                textAlign = TextAlign.Center,
                color = colorResource(id = R.color.onClickSellBack),
                fontSize = 30.sp
            )
        }


        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
                .padding(10.dp),
            text = totalValuePrice,
            color = colorResource(id = R.color.pozitive),
            fontSize = 24.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(id = R.color.light_grey))
                .height(1.dp)
        ) {}



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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(modifier = Modifier.weight(1f), text = "")
            Text(
                modifier = Modifier.weight(2f),
                text = stringResource(id = R.string.Symbol),
                textAlign = TextAlign.Start,
                color = Color.Red,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.weight(3f),
                text = stringResource(id = R.string.Amount),
                textAlign = TextAlign.Start,
                color = Color.Red,
                fontSize = 16.sp
            )
            Text(
                modifier = Modifier.weight(2f),
                text = stringResource(id = R.string.Value),
                textAlign = TextAlign.Start,
                color = Color.Red,
                fontSize = 16.sp
            )

        }

        if (searchedItem.isEmpty()) {
            myCoins?.let {
                val context = LocalContext.current
                WalletItemComposeView(it) { itemName ->
                    val coinName = solveCoinName(itemName)
                    SharedPreferencesManager(context).addSharedPreferencesString("coinName", coinName)
                    openTradePage.invoke(itemName)
                }
            }
        } else {
            val context = LocalContext.current
            WalletItemComposeView(resultItems) { itemName ->
                val coinName = solveCoinName(itemName)
                SharedPreferencesManager(context).addSharedPreferencesString("coinName", coinName)
                openTradePage.invoke(itemName)
            }
        }

    }
}

private fun getSearchedList (searchedItem: String, itemList: ArrayList<NewModelForItemHistory>?): List<NewModelForItemHistory> {
    val queryCoin = searchedItem.uppercase(Locale.getDefault())

    if (queryCoin.isEmpty()) return itemList?.map { it } ?: emptyList()

    return itemList?.filter { item ->
        item.CoinName.contains(queryCoin, ignoreCase = true)
    } ?: emptyList()
}