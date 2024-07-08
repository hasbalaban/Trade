package com.finance.trade_learn.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel.Companion.cachedData
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TradeScreen() {
    val viewModel = LocalViewModelHistoryTrade.current

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getDataFromDatabase(context)
    }

    val trades = viewModel.listOfTrade.observeAsState(emptyList()).value
    MainContent(trades = trades)
}

@Composable
private fun MainContent(trades: List<SaveCoin>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Alım Satım İşlemleri") },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.LightGray)
                .padding(8.dp)
                .clip(shape = RoundedCornerShape(16.dp))
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

    val imageUrl = cachedData.firstOrNull {
        it.name.contains(trade.coinName, ignoreCase = true)
    }?.image


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp)), // Yuvarlatılmış kenarlar
        elevation = 4.dp,
        backgroundColor = Color.White // Card arka plan rengi
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val painter =
                rememberAsyncImagePainter(
                    ImageRequest.Builder
                        (LocalContext.current).data(
                        data = imageUrl
                    ).apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.error)
                    }).build()
                )

            Image(
                painter = painter,
                contentDescription = null, // Opsiyonel içerik açıklaması
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop // İçeriği sınırlar içine sığdır
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Metin içeriği
            Column {
                Text(
                    text = trade.coinName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary // Başlık rengi
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Amount: ${trade.coinAmount.toDouble().formatAmount()}",
                    fontSize = 16.sp,
                    color = Color.DarkGray // Metin rengi
                )
                Text(
                    text = "Price: ${trade.coinPrice.toDouble().formatPrice()}",
                    fontSize = 16.sp,
                    color = Color.DarkGray // Metin rengi
                )
                Text(
                    text = "Total Cost: ${trade.total.toDouble().formatTotalCost()}",
                    fontSize = 16.sp,
                    color = Color.DarkGray // Metin rengi
                )

                // Date formatını burada düzeltelim
                val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                    SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).parse(trade.date)
                )


                Text(
                    text = "Date: $formattedDate",
                    fontSize = 14.sp,
                    color = Color.DarkGray // Metin rengi
                )
                Text(
                    text = "Operation: ${
                        if (trade.tradeOperation.equals(
                                "Buy",
                                ignoreCase = true
                            )
                        ) "Alış" else "Satış"
                    }",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray // Metin rengi
                )
            }
        }
    }
}

// Extensions for formatting
fun Double.formatAmount(): String = "%.6f".format(this)
fun Double.formatPrice(): String = "%.6f".format(this)
fun Double.formatTotalCost(): String = "%.3f".format(this)

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
            System.currentTimeMillis().toString(),
            "Buy"
        ),
        SaveCoin(
            2,
            "Ethereum",
            "10.0",
            "2300.0",
            "23000.0",
            (System.currentTimeMillis() - 86400000).toString(),
            "sell"
        )
    )
    MainContent(sampleTradeData)
}
