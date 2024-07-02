package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
fun PopularCoinCard(
    coin: CoinsHome,
    modifier: Modifier = Modifier,
    clickedItem: (String) -> Unit
) {

    val configuration = LocalConfiguration.current
    val cardWidth = remember {
        (configuration.screenWidthDp / 2.7).dp
    }

    Card(
        modifier = modifier
            .clickable { clickedItem.invoke(coin.id) }
            .width(cardWidth)
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Coin Image
                Image(
                    painter = rememberAsyncImagePainter(coin.CoinImage),
                    contentDescription = coin.CoinName,
                    modifier = Modifier
                        .size(32.dp) // Daha kompakt boyut
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Coin Change Percentage and Arrow
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = coin.CoinChangePercente,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (coin.CoinChangePercente.contains("+"))
                            Color(0xFF4CAF50) else Color(0xFFF44336),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))


            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = coin.CoinName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = coin.coinSymbol.uppercase(),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "$${coin.CoinPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Image(
                    modifier = Modifier
                        .rotate(
                            if (coin.CoinChangePercente.contains("+"))
                                0.0f
                            else if (coin.CoinChangePercente.contains("-"))
                                90.0f
                            else 0.0f
                        )
                        .size(16.dp), // Daha küçük boyut
                    painter = painterResource(id = R.drawable.arrow_outward),
                    contentDescription = "price change",
                    colorFilter = ColorFilter.tint(
                        if (coin.CoinChangePercente.contains("+"))
                            Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PopularCoinScreenPreview() {
    MaterialTheme {
        val coinItem = CoinsHome(
            id = "1",
            CoinName = "FLUX / USD",
            coinSymbol = "FLUX",
            CoinPrice = "0.636005",
            CoinChangePercente = "+1.10",
            CoinImage = "https://coin-images.coingecko.com/coins/images/5163/large/Flux_symbol_blue-white.png?1696505679",
            raise = enumPriceChange.notr,
            marketCap = "221975378",
            total_volume = "221975378"
        )
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                PopularCoinCard(coinItem, modifier = Modifier) {}
            }
        }
    }
}
