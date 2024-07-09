package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun PopularCoinCard(
    coin: CoinsHome,
    modifier: Modifier = Modifier,
    clickedItem: (String) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .width(150.dp)
            .clickable { clickedItem(coin.CoinName) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(2.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Image(
                painter = rememberAsyncImagePainter(model = coin.CoinImage),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = coin.CoinName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$${coin.CoinPrice}",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            val priceChangeColor = if (coin.CoinChangePercente.contains("+")) Color(0xFF4CAF50) else Color(0xFFF44336)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow_outward),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                        .rotate(
                            if (coin.CoinChangePercente.contains("+"))
                                0.0f
                            else if (coin.CoinChangePercente.contains("-"))
                                90.0f
                            else 0.0f
                        ),
                    colorFilter = ColorFilter.tint(priceChangeColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${coin.CoinChangePercente}%",
                    fontSize = 12.sp,
                    color = priceChangeColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewPopularCoinCard() {
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
    PopularCoinCard(coin = coinItem) {}
}
