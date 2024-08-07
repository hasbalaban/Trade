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
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.view.CoinProgress
import com.finance.trade_learn.view.trade.TradePageUiState
import com.finance.trade_learn.viewModel.TradeViewModel


private val LocalTradePageViewModel = compositionLocalOf<TradeViewModel> { error("No BaseViewModel found") }

@Composable
fun TradePage(itemName: String) {
    val viewModel = hiltViewModel<TradeViewModel>()

    val detailOfItem = viewModel.getItemInfo(itemName).observeAsState()
    val userBalance = viewModel.getItemInfo("tether").observeAsState()

    userBalance.value?.let {
        viewModel.setUserBalance(it)
    }

    detailOfItem.value?.let {
        viewModel.setDetailsOfCoinFromDatabase(it)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getSelectedCoinDetails(itemName)
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

    var price by remember { mutableDoubleStateOf(0.0) }
    val itemCurrentInfo by viewModel.itemCurrentInfo.collectAsState()
    when (val item = itemCurrentInfo) {
        is TradePageUiState.Data -> {
            price = item.data.current_price ?: 0.0
        }
        else -> {}
    }

    var availableAmount by remember { mutableDoubleStateOf(0.0) }
    val availableItemInfo by viewModel.availableItemInfo.collectAsState()
    when (val item = availableItemInfo) {
        is TradePageUiState.Data -> {
            availableAmount = item.data?.CoinAmount ?: 0.0
        }
        else -> {}
    }

    var userBalance by remember { mutableDoubleStateOf(0.0) }
    val userInfo by viewModel.userBalance.collectAsState()
    when (val item = userInfo) {
        is TradePageUiState.Data -> {
            userBalance = item.data?.CoinAmount ?: 0.0
        }
        else -> {}
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
            BalanceSection(
                totalBalance = userBalance
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (amountToTrade > 0) {
                            performBuyAction(amountToTrade, viewModel = viewModel)
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
                            performSellAction(amountToTrade, viewModel = viewModel)
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
        when (val item = itemCurrentInfo) {
            is TradePageUiState.Error -> {
                // Handle error state
            }
            TradePageUiState.Loading -> {
                // Handle loading state
            }
            is TradePageUiState.Data -> {
                Image(
                    painter = rememberAsyncImagePainter(item.data.image),
                    contentDescription = stringResource(id = R.string.cripto_image),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shape = CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.data.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material.MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))

                val priceChangeColor = if ((item.data.price_change_percentage_24h ?: 0.0) >= 0.0) Color(0xFF4CAF50) else Color(0xFFF44336)

                Text(
                    text = stringResource(id = R.string.daily_change) + "%.2f%%".format(item.data.price_change_percentage_24h),
                    style = MaterialTheme.typography.bodyLarge,
                    color = priceChangeColor,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.price) + ": ${item.data.current_price} USD",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material.MaterialTheme.colors.onPrimary
                )
            }
        }

        when (val item = availableItemInfo) {
            TradePageUiState.Loading -> {
                // Handle loading state for available item info
            }
            is TradePageUiState.Data -> {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.available_amount) + "%.6f USD".format(item.data?.CoinAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material.MaterialTheme.colors.onPrimary
                )
            }
            is TradePageUiState.Error -> {
                // Handle error state for available item info
            }
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
    var currentPrice by remember { mutableDoubleStateOf(0.0) }

    when (val item = itemCurrentInfo) {
        is TradePageUiState.Data -> {
            currentPrice = item.data.current_price ?: 0.0
        }

        else -> {}
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
    totalBalance: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.total_balance) +  "%.4f USD".format(totalBalance),
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material.MaterialTheme.colors.onPrimary
        )
    }
}


private fun performBuyAction(
    amountToTrade: Double,
    viewModel: TradeViewModel
) {
    val isAvailableToBuy = viewModel.compare(amount = amountToTrade, tradeState = TradeType.Buy)

    if(isAvailableToBuy){
        viewModel.operationTrade(
            itemAmount = amountToTrade,
            tradeType = TradeType.Buy
        )
    }

    //Toast.makeText(context, "Alım işlemi gerçekleştirildi: $amountToTrade", Toast.LENGTH_SHORT).show()
}

private fun performSellAction(amountToTrade: Double, viewModel: TradeViewModel) {
    val isAvailableToBuy = viewModel.compare(amount = amountToTrade, tradeState = TradeType.Sell)

    if(isAvailableToBuy){
        viewModel.operationTrade(
            itemAmount = amountToTrade,
            tradeType = TradeType.Sell
        )
    }
    //Toast.makeText(context, "Satış işlemi gerçekleştirildi: $amountToTrade", Toast.LENGTH_SHORT).show()
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
