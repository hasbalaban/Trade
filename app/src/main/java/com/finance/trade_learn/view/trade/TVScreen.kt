package com.finance.trade_learn.view.trade


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.view.LocalTvPageViewModel
import com.finance.trade_learn.view.coin.ItemIcon


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

        val buySellScreenData = viewModel.tradePageUiState.value.data.copy(
            _balance = balance,
            _ownedShares = selectedItemBalance
        )
        viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))

    } else {
        val localItemList = viewModel.getItemInfo().observeAsState()
        val balance = localItemList.value?.firstOrNull {
            it.CoinName == "tether"
        }?.CoinAmount ?: 0.0
        val selectedItemBalance = localItemList.value?.firstOrNull {
            it.CoinName == itemId
        }?.CoinAmount ?: 0.0

        val buySellScreenData = viewModel.tradePageUiState.value.data.copy(
            _balance = balance,
            _ownedShares = selectedItemBalance
        )
        viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))
    }

    BuySellUnifiedScreen(goBack = goBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySellUnifiedScreen(goBack: () -> Unit) {
    val viewModel = LocalTvPageViewModel.current

    val tradePageUiState = viewModel.tradePageUiState.collectAsState()
    val quantity by remember { derivedStateOf { tradePageUiState.value.data.transactionAmount} }

    val animatedBalance by animateFloatAsState(
        targetValue = tradePageUiState.value.data.balance.toFloat(),
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ), label = ""
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
                .padding(top = 24.dp)
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    goBack.invoke()
                }, modifier = Modifier.padding(8.dp)
            ) {
                androidx.compose.material3.Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            Text(
                modifier = Modifier.weight(1f),
                text = viewModel.selectedItemDetail.value.data?.name.toString() + " - " + viewModel.selectedItemDetail.value.data?.symbol.toString() ,
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center
            )

            viewModel.selectedItemDetail.value.data?.let {
                ItemIcon(
                    imageUrl = it.image,
                    itemName = it.name,
                    modifier = Modifier.size(48.dp)
                )
            }
        }


        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {


            Column {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance: ", style = TextStyle(
                            fontSize = 20.sp, fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colors.onPrimary
                        )
                    )

                    Text(
                        text = "$animatedBalance $",
                        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Daily Change: ${tradePageUiState.value.data.dailyPercentChange}%",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Owned Shares: ${tradePageUiState.value.data.ownedAmount}",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )


            }


            Row(
                modifier = Modifier.fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ){
                Text(
                    text = "Price: ", style = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.onPrimary
                    )
                )
                Text(
                    text = tradePageUiState.value.data.currentPrice.toString(),
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colors.onPrimary.copy(0.2f))

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {

            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = "Quantity", style = TextStyle(
                    fontSize = 20.sp, fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colors.onPrimary
                )
            )

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp),
                color = MaterialTheme.colors.onPrimary.copy(0.2f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clickable {
                            if (quantity > 0) {

                                val changeRate =
                                    if (tradePageUiState.value.data.currentPrice
                                            .toString()
                                            .toDoubleOrZero() > 50.0
                                    ) 0.001 else 1.0

                                val buySellScreenData =
                                    viewModel.tradePageUiState.value.data.copy(
                                        _transactionAmount = quantity - changeRate
                                    )
                                viewModel.changeViewState(
                                    viewModel.tradePageUiState.value.copy(
                                        data = buySellScreenData
                                    )
                                )
                            }

                        },
                    tint = MaterialTheme.colors.onPrimary
                )
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clickable {
                            val changeRate =
                                if (tradePageUiState.value.data.currentPrice
                                        .toString()
                                        .toDoubleOrZero() > 50.0
                                ) 0.001 else 1.0

                            val buySellScreenData =
                                viewModel.tradePageUiState.value.data.copy(_transactionAmount = quantity + changeRate)
                            viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))
                        },
                    tint = MaterialTheme.colors.onPrimary

                )
            }

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 12.dp, end = 4.dp),
                color = MaterialTheme.colors.onPrimary.copy(0.2f)
            )



            OutlinedTextField(
                modifier = Modifier
                    .padding(end = 12.dp, top = 6.dp)
                    .weight(1f),
                value = quantity.toString(),
                onValueChange = {
                    val newValue = it.toDoubleOrNull() ?: return@OutlinedTextField


                    val buySellScreenData =
                        viewModel.tradePageUiState.value.data.copy(_transactionAmount = newValue.coerceAtLeast(0.0))
                    viewModel.changeViewState(viewModel.tradePageUiState.value.copy(data = buySellScreenData))
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.Amount),
                        color = MaterialTheme.colors.onPrimary,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),

                textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colors.onPrimary,
                    unfocusedTextColor = MaterialTheme.colors.onPrimary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onPrimary
                )
            )
        }


        HorizontalDivider(
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colors.onPrimary.copy(0.2f)
        )


        Text(
            modifier = Modifier
                .padding(end = 24.dp)
                .fillMaxWidth(),
            text = "Total: ${tradePageUiState.value.data.totalTransactionCost}",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.End
        )


        // Buy and Sell Buttons
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 56.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                enabled = tradePageUiState.value.data.isBuyEnabled,
                onClick = {
                    viewModel.clickedBuy()
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.pozitive),
                    contentColor = Color.White
                )
            ) {
                Text(text = "BUY", style = TextStyle(fontSize = 16.sp))
            }


            Button(
                enabled = tradePageUiState.value.data.isSellEnabled,
                onClick = {
                    viewModel.clickedSell()
                },
                modifier = Modifier
                    .padding(start = 8.dp)
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

// Extension function to safely convert string to double
fun String.toDoubleOrZero(): Double {
    return this.replace(",", ".").toDoubleOrNull() ?: 0.0
}

@Composable
@Preview
fun PreviewBuySellUnifiedScreen() {
    BuySellUnifiedScreen(goBack = {})
}
