package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.finance.trade_learn.R
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome

@Composable
fun CoinItemScreen(coin: CoinsHome, clickedItem: (String) -> Unit) {
    Card(
        modifier = Modifier
            .clickable { clickedItem.invoke(coin.id) }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(coin.CoinImage),
                contentDescription = coin.CoinName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coin.CoinName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = coin.coinSymbol.uppercase(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Market Cap: ${coin.marketCap}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${coin.CoinPrice}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = coin.CoinChangePercente,
                        fontSize = 12.sp,
                        color = if (coin.CoinChangePercente.contains("+"))
                            Color(0xFF4CAF50) else Color(0xFFF44336)
                    )

                    Image(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .rotate(
                                if (coin.CoinChangePercente.contains("+"))
                                    0.0f
                                else 90.0f
                            )
                            .size(16.dp),
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
}

@Composable
fun PreviewCoinItemScreen() {
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
    CoinItemScreen(coinItem) {}
}
