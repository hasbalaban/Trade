package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.models.modelsConvector.CoinsHome

@Composable
fun CoinDetailScreen(coin: CoinsHome, clickedItem: (String) -> Unit) {
    Card(
        modifier = Modifier
            .clickable {
                clickedItem.invoke(coin.CoinName)
            }
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.White),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(coin.CoinImage),
                    contentDescription = coin.CoinName,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Gray, shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                    Text(
                        text = coin.CoinName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = coin.coinSymbol.uppercase(),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }


                Text(
                    text = coin.CoinPrice.let { "$${it}" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Market Cap: ${coin.marketCap}",
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = "24h Change: ${coin.CoinChangePercente.let { "${it}%" }}",
                fontSize = 14.sp,
                color = if (coin.CoinChangePercente >= 0.0.toString())
                    Color.Green else Color.Red
            )
        }
    }
}
