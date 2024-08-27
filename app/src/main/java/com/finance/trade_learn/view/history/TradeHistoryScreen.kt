package com.finance.trade_learn.view.history

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel.Companion.allCryptoItems
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions
import com.finance.trade_learn.view.LocalViewModelHistoryTrade
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeHistoryScreen(goBack: () -> Unit) {

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary)){
        Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.CenterStart){
            IconButton(
                onClick = {
                    goBack.invoke()
                }, modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.buy_sel_operations_text),
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
        }
        MainContent(goBack = goBack)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun MainContent(goBack: () -> Unit) {
    val viewModel = LocalViewModelHistoryTrade.current
    val transactions by viewModel.transactionHistoryResponse.collectAsState()
    val transactionViewState by viewModel.transactionViewState.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val isLogin = true
        if (isLogin) viewModel.getTransactionHistory()
        else viewModel.getDataFromDatabase(context)
    }

    Box(modifier = Modifier .fillMaxSize(), contentAlignment = Alignment.Center) {


        if (transactions.data?.isEmpty() == true){
            EmptyTransactionHistoryScreen(onGoBackClick = goBack)
        }else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(transactions.data ?: emptyList()) { trade ->
                        TradeItem(trade)
                        HorizontalDivider(
                            modifier = Modifier
                                .alpha(0.5f)
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        if (transactionViewState.isLoading) {
            CircularProgressIndicator(
                color = Color(0xff3B82F6),
                strokeWidth = 4.dp
            )
        }
    }
}

@Composable
fun TradeItem(trade: UserTransactions) {
    val imageUrl = allCryptoItems.firstOrNull {
        it.id.contains(trade.transactionItemName, ignoreCase = true)
    }?.image

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .apply {
                        crossfade(true)
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.error)
                    }
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = trade.transactionItemName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary // Title color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.amount) + trade.amount.toDouble()
                            .formatAmount(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.price) + ": ${
                            trade.price.toDouble().formatPrice()
                        }",
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.total) + ": ${
                            trade.transactionTotalPrice.toDouble().formatTotalCost()
                        }",
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.date) + ": ${trade.date.formatDate()}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row {

                    Text(
                        text = stringResource(id = R.string.operation_text),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary // Default text color
                    )
                    Text(
                        text = if (trade.transactionType.equals(
                                stringResource(id = R.string.buy),
                                ignoreCase = true
                            )
                        ) {
                            stringResource(id = R.string.buy)
                        } else {
                            stringResource(id = R.string.sell)
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                        if (trade.transactionType.equals(
                                stringResource(id = R.string.buy),
                                ignoreCase = true
                            )
                        )
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    )
                }


            }
        }
    }
}


@Composable
fun EmptyTransactionHistoryScreen(
    onGoBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Transactions Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "It seems like you haven't made any transactions yet.",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onGoBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary)
        ) {
            Text(text = "Go Back", fontSize = 18.sp, color = MaterialTheme.colors.primary)
        }
    }
}


// Extensions for formatting
fun Double.formatAmount(): String = "%.6f".format(this)
fun Double.formatPrice(): String = "%.6f".format(this)
fun Double.formatTotalCost(): String = "%.3f".format(this)
fun String.formatDate(): String {
    val inputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    val date = try {
        inputFormat.parse(this)
    }catch (_ : Exception){
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date(this.toLong())
        val formattedDate = sdf.format(date)
        inputFormat.parse(formattedDate)
    }

    return outputFormat.format(date)
}

@Preview(showBackground = true)
@Composable
fun PreviewTradeScreen() {
    val sampleTradeData = listOf(
        UserTransactions(
            1,
            "Bitcoin",
            "0.5",
            "35000.0",
            "17500.0",
            "Buy",
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),

            ),
        UserTransactions(
            2,
            "Ethereum",
            "10.0",
            "2300.0",
            "23000.0",
            "sell",
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm:ss",
                Locale.getDefault()
            ).format(Date(System.currentTimeMillis() - 86400000)),
        )
    )
    TradeItem(sampleTradeData.first())
}
