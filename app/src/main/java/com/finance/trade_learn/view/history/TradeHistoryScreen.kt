package com.finance.trade_learn.view.history

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel.Companion.allCryptoItems
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.view.LocalViewModelHistoryTrade
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TradeHistoryScreen(modifier: Modifier) {
    val viewModel = LocalViewModelHistoryTrade.current

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getDataFromDatabase(context)
    }

    val trades = viewModel.listOfTrade.observeAsState(emptyList()).value
    MainContent(trades = trades, modifier = modifier)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun MainContent(trades: List<SaveCoin>, modifier: Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Alım Satım İşlemleri", color = MaterialTheme.colors.onPrimary) },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = Color(0xFFADA8A8))
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(trades) { trade ->
                    TradeItem(trade)
                    Spacer(modifier = Modifier.height(8.dp)) // İtemlar arasına boşluk ekleyelim
                }
            }
        }
    }
}

@Composable
fun TradeItem(trade: SaveCoin) {
    val imageUrl = allCryptoItems.firstOrNull {
        it.name.contains(trade.coinName, ignoreCase = true)
    }?.image

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    text = trade.coinName,
                    fontSize = 18.sp,
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
                        text = "Amount: ${trade.coinAmount.toDouble().formatAmount()}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Price: ${trade.coinPrice.toDouble().formatPrice()}",
                        fontSize = 14.sp,
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
                        text = "Total: ${trade.total.toDouble().formatTotalCost()}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Date: ${trade.date.formatDate()}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onSurface // Text color
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row {

                    Text(
                        text = "Operation: ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onPrimary // Default text color
                    )
                    Text(
                        text = if (trade.tradeOperation.equals("Buy", ignoreCase = true)) {
                            "Alış"
                        } else {
                            "Satış"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                        if (trade.tradeOperation.equals("Buy", ignoreCase = true))
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    )
                }


            }
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
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}

@Preview(showBackground = true)
@Composable
fun PreviewTradeScreen() {
    val sampleTradeData = listOf(
        SaveCoin(
            1,
            "Bitcoin",
            "0.5",
            "35000.0",
            "17500.0",
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
            "Buy"
        ),
        SaveCoin(
            2,
            "Ethereum",
            "10.0",
            "2300.0",
            "23000.0",
            SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(System.currentTimeMillis() - 86400000)),
            "sell"
        )
    )
    MainContent(sampleTradeData, modifier = Modifier)
}
