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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.R
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome

@Composable
fun CoinItemScreen(coin: CoinsHome, clickedItem: (String) -> Unit) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .background(Color.White)
            .clickable { clickedItem.invoke(coin.id) }
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(coin.CoinImage),
                contentDescription = coin.CoinName,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = coin.CoinName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = coin.coinSymbol.uppercase(),
                    fontSize = 14.sp,
                    color = onSurfaceColor.copy(alpha = 0.6f)
                )
            }

            Text(
                modifier = Modifier.height(40.dp),
                text = coin.CoinPrice.let { "$${it}" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Market Cap: ${coin.marketCap}",
                    fontSize = 12.sp,
                    color = onSurfaceColor
                )
                Text(
                    text = "24h Change: ${coin.CoinChangePercente}",
                    fontSize = 14.sp,
                    color = if (coin.CoinChangePercente.contains("+"))
                        Color(0xFF2ebd85) else Color(0xFFFF0000)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.arrow_outward),
                contentDescription = "price raised",
                colorFilter = ColorFilter.tint(
                    if (coin.CoinChangePercente.contains("+"))
                        Color(0xFF2ebd85) else Color(0xFFFF0000)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color.LightGray)
    }
}

@Preview
@Composable
private fun CoinItemScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.background(Color.White)) {
            val coinItem = CoinsHome(
                id = "1",
                CoinName = "FLUX / USD",
                coinSymbol = "FLUX / USD",
                CoinPrice = "0.636005",
                CoinChangePercente = "+1.10",
                CoinImage = "https://coin-images.coingecko.com/coins/images/5163/large/Flux_symbol_blue-white.png?1696505679",
                raise = enumPriceChange.notr,
                marketCap = "221975378",
                total_volume = "221975378"
            )
            CoinItemScreen(coinItem) {}
        }
    }
}
