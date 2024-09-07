package com.finance.trade_learn.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.view.trade.formatToDecimals


@Composable
fun MainBuySellScreen(itemId: String, goBack: () -> Unit) {
    val viewModel = LocalTvPageViewModel.current

    LaunchedEffect(key1 = Unit) {
        viewModel.setSelectedCoinDetails(itemId)
    }

    val isLogin = BaseViewModel.isLogin.value
    if (isLogin) {
        val userInfo = BaseViewModel.userInfo.collectAsState()
        val balance = userInfo.value.data?.balances?.firstOrNull { it.itemName == "tether" }?.amount
            ?: 0.0
        val selectedItemBalance = userInfo.value.data?.balances?.firstOrNull {
            it.itemName == itemId
        }?.amount ?: 0.0

        val buySellScreenData = viewModel.tradePageUiState.value.data.copy(_balance = balance, _ownedShares = selectedItemBalance)
        viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))

    } else {
        val localItemList = viewModel.getItemInfo().observeAsState()
        val balance = localItemList.value?.firstOrNull {
            it.CoinName == "tether"
        }?.CoinAmount ?: 0.0
        val selectedItemBalance = localItemList.value?.firstOrNull {
            it.CoinName == itemId
        }?.CoinAmount ?: 0.0

        val buySellScreenData = viewModel.tradePageUiState.value.data.copy(_balance = balance, _ownedShares = selectedItemBalance)
        viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))
    }

    BuySellUnifiedScreen()
}

@Composable
fun BuySellUnifiedScreen() {
    val viewModel = LocalTvPageViewModel.current

    val tradePageUiState = viewModel.tradePageUiState.collectAsState()


    val quantity by remember {
        derivedStateOf { tradePageUiState.value.data.transactionAmount.formatToDecimals(5) }
    }
    val stockIconResId = R.drawable.icon_notifications // Replace with actual stock icon resource

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        // Upper section with stock icon, daily change, current price, owned shares info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = stockIconResId),
                contentDescription = "Stock Icon",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(48.dp)
            )
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Balance: ${tradePageUiState.value.data.balance}",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onPrimary
                )


                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = "Daily Change: ${tradePageUiState.value.data.dailyPercentChange}%",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "Current Price: ${tradePageUiState.value.data.currentPrice}",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "Owned Shares: ${tradePageUiState.value.data.ownedShares}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }



        Column {
            // Price input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text(
                        text = "Price: ", style = TextStyle(
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    Text(
                        text = tradePageUiState.value.data.currentPrice.toString(), style = TextStyle(fontSize = 16.sp),
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                color = MaterialTheme.colors.onPrimary.copy(0.5f)
            )


            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
            ){
                // Quantity section with +/- icons
                Text(

                    modifier = Modifier
                        .padding(top = 16.dp),

                    text = "Quantity", style = TextStyle(
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onPrimary
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (quantity > 0) {

                                    val changeRate =
                                        if (tradePageUiState.value.data.currentPrice.toString().toDoubleOrZero() > 50.0) 0.001 else 1.0

                                    val buySellScreenData = viewModel.tradePageUiState.value.data.copy(_transactionAmount = quantity - changeRate)
                                    viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))
                                }

                            },
                        tint = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = quantity.toString(),
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colors.onPrimary
                    )
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                val changeRate =
                                    if (tradePageUiState.value.data.currentPrice.toString().toDoubleOrZero() > 50.0) 0.001 else 1.0

                                val buySellScreenData = viewModel.tradePageUiState.value.data.copy(_transactionAmount = quantity + changeRate)
                                viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))


                            },
                        tint = MaterialTheme.colors.onPrimary

                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier.fillMaxWidth().padding(end = 24.dp),
                    text = "Total: ${tradePageUiState.value.data.totalTransactionCost}",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.End
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buy and Sell Buttons
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 56.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Button(
                        onClick = { /* Handle Buy Action */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.pozitive),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "BUY", style = TextStyle(fontSize = 16.sp))
                    }
                    Button(
                        onClick = { /* Handle Sell Action */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.negative),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "SELL", style = TextStyle(fontSize = 16.sp))
                    }
                }
            }
        }


    }
}

// Extension function to safely convert string to double
fun String.toDoubleOrZero(): Double {
    return this.replace(",", ".").toDoubleOrNull() ?: 0.0
}

@Composable
@Preview
fun PreviewBuySellUnifiedScreen() {
    BuySellUnifiedScreen()
}
