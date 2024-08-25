import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.view.CoinProgress
import com.finance.trade_learn.viewModel.TradeViewModel


private val LocalTradePageViewModel = compositionLocalOf<TradeViewModel> { error("No BaseViewModel found") }

@Composable
fun TradePage(itemName: String) {
    val viewModel = hiltViewModel<TradeViewModel>()


    LaunchedEffect(key1 = Unit) {
        viewModel.getSelectedCoinDetails(itemName)
    }

    val isLogin = BaseViewModel.isLogin.value
    if (isLogin) {
        val userInfo = BaseViewModel.userInfo.collectAsState()
        LaunchedEffect(userInfo) {
            val accountBalance = MyCoins(
                CoinName = "tether",
                CoinAmount = userInfo.value.data?.balances?.firstOrNull { it.itemName == "tether" }?.amount  ?: 0.0
            )

            userInfo.value.data?.balances?.firstOrNull { it.itemName == "tether" }
            viewModel.setUserBalance(accountBalance)

            val selectedItemBalance = userInfo.value.data?.balances?.firstOrNull {
                it.itemName == itemName
            }
            selectedItemBalance?.let {
                val item = MyCoins(
                    CoinName = selectedItemBalance.itemName,
                    CoinAmount = selectedItemBalance.amount
                )
                viewModel.setDetailsOfCoinFromDatabase(item)
            }
        }
    } else {
        val detailOfItem = viewModel.getItemInfo(itemName).observeAsState()
        val userBalance = viewModel.getItemInfo("tether").observeAsState()

        userBalance.value?.let {
            viewModel.setUserBalance(it)
        }

        detailOfItem.value?.let {
            viewModel.setDetailsOfCoinFromDatabase(it)
        }
    }






    CompositionLocalProvider(LocalTradePageViewModel provides viewModel) {
        TradeMainScreen()
    }
}

@Composable
private fun TradeMainScreen(
    viewModel: TradeViewModel = LocalTradePageViewModel.current,
) {
    var amountToTrade by remember { mutableDoubleStateOf(0.0) }

    val itemCurrentInfo by viewModel.itemCurrentInfo.collectAsState()
    val price by remember {
        derivedStateOf {
            itemCurrentInfo.data?.current_price ?: 0.0
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material.MaterialTheme.colors.primary)
    ) {
        Text(
            text = stringResource(id = R.string.cripto_buy_sel) ,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 24.dp)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            ItemDetailSection()

            Spacer(modifier = Modifier.height(16.dp))
            TradeAmountInput(
                amountToTrade = amountToTrade,
                onAmountChange = {
                    amountToTrade = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            TotalCostSection(
                totalCost = amountToTrade * price
            )

            Spacer(modifier = Modifier.height(6.dp))
            BalanceSection(viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (amountToTrade > 0) {
                            viewModel.buyCoin(amountToTrade)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.textBuy),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Button(
                    onClick = {
                        if (amountToTrade > 0) {
                            viewModel.sellCoin(amountToTrade)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(androidx.compose.material.MaterialTheme.colors.error),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.textSell),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onError
                    )
                }

            }
    }
    }
}

@Composable
fun ItemDetailSection(
    viewModel: TradeViewModel = LocalTradePageViewModel.current
) {
    val itemCurrentInfo by viewModel.itemCurrentInfo.collectAsState()
    val availableItemInfo by viewModel.availableItemInfo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        itemCurrentInfo.data?.let {
            Image(
                painter = rememberAsyncImagePainter(it.image),
                contentDescription = stringResource(id = R.string.cripto_image),
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = itemCurrentInfo.data?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.material.MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))

            val priceChangeColor = if ((it.price_change_percentage_24h ?: 0.0) >= 0.0) Color(0xFF4CAF50) else Color(0xFFF44336)

            Text(
                text = stringResource(id = R.string.daily_change) + "%.2f%%".format(it.price_change_percentage_24h),
                style = MaterialTheme.typography.bodyLarge,
                color = priceChangeColor,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.price) + ": ${it.current_price} USD",
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material.MaterialTheme.colors.onPrimary
            )
        }


        availableItemInfo.data?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.available_amount) + "%.6f USD".format(it.CoinAmount),
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material.MaterialTheme.colors.onPrimary
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeAmountInput(
    viewModel: TradeViewModel = LocalTradePageViewModel.current,
    amountToTrade: Double,
    onAmountChange: (Double) -> Unit
) {
    var textFieldValue by remember { mutableDoubleStateOf(0.0) }

    val itemCurrentInfo by viewModel.itemCurrentInfo.collectAsState()
    val currentPrice by remember {
        derivedStateOf {
            itemCurrentInfo.data?.current_price ?: 0.0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.Amount),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    val changeRate = if (currentPrice > 50.0) 0.001 else 1.0

                    textFieldValue  = viewModel.changeAmounts(amountToTrade.coerceAtLeast(0.0), changeRate, CoinProgress.MINUS)
                    onAmountChange(textFieldValue)
                }
            ) {
                Icon(Icons.Default.Remove, contentDescription = stringResource(id = R.string.Decrease), tint = androidx.compose.material.MaterialTheme.colors.onPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = textFieldValue.toString(),
                onValueChange = {
                    textFieldValue = it.toDoubleOrNull() ?: 0.0
                    onAmountChange(textFieldValue)
                },
                label = { Text(text = stringResource(id = R.string.Amount) , color = androidx.compose.material.MaterialTheme.colors.onPrimary) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = androidx.compose.material.MaterialTheme.colors.onPrimary,
                    unfocusedTextColor = androidx.compose.material.MaterialTheme.colors.onPrimary,
                    focusedBorderColor = androidx.compose.material.MaterialTheme.colors.onPrimary,
                    unfocusedBorderColor = androidx.compose.material.MaterialTheme.colors.onPrimary,
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    val changeRate = if (currentPrice > 50.0) 0.001 else 1.0

                    textFieldValue  = viewModel.changeAmounts(amountToTrade.coerceAtLeast(0.0), changeRate, CoinProgress.SUM)
                    onAmountChange(textFieldValue)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.increase), tint = androidx.compose.material.MaterialTheme.colors.onPrimary)
            }
        }
    }
}

@Composable
fun TotalCostSection(
    totalCost: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.total_cost_text),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.total_cost) + "%.4f USD".format(totalCost),
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun BalanceSection(
    viewModel: TradeViewModel
) {
    val userInfo by viewModel.userBalance.collectAsState()
    val userBalance by remember {
        derivedStateOf {
            userInfo?.CoinAmount ?: 0.0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.balance) + "  " +  "%.4f USD".format(userBalance),
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
    }
}

@Preview
@Composable
private fun CoinItemScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.background(androidx.compose.material.MaterialTheme.colors.background)) {
            TradePage(itemName = "btc")
        }
    }
}
