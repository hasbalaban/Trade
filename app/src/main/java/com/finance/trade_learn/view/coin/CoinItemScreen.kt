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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                .padding(horizontal = 12.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(coin.CoinImage),
                contentDescription = coin.CoinName,
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = coin.CoinName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = coin.coinSymbol.uppercase(),
                    fontSize = 12.sp,
                    color = onSurfaceColor.copy(alpha = 0.6f)
                )
            }

            Text(
                text = coin.CoinPrice.let { "$${it}" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = onSurfaceColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Market Cap: ${coin.marketCap}",
                fontSize = 12.sp,
                color = onSurfaceColor
            )


            Row (horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "24h: ${coin.CoinChangePercente}",
                    fontSize = 12.sp,
                    color = if (coin.CoinChangePercente.contains("+"))
                        Color(0xFF2ebd85) else Color(0xFFFF0000)
                )

                Image(
                    modifier = Modifier.padding(start = 10.dp).rotate(
                        if (coin.CoinChangePercente.contains("+"))
                            0.0f
                        else if (coin.CoinChangePercente.contains("-"))
                            90.0f
                        else  0.0f
                    ),
                    painter = painterResource(id = R.drawable.arrow_outward),
                    contentDescription = "price raised",
                    colorFilter = ColorFilter.tint(
                        if (coin.CoinChangePercente.contains("+"))
                            Color(0xFF2ebd85) else Color(0xFFFF0000)
                    )
                )
            }

        }

        Spacer(modifier = Modifier.height(6.dp))
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
