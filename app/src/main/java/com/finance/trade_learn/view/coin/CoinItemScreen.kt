package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.finance.trade_learn.R
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome

@Composable
fun CoinItemScreen(coin: CoinsHome, clickedItem: (String) -> Unit) {

    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(coin.CoinImage)
            .apply {
                crossfade(true)
                placeholder(R.drawable.placeholder)
                error(R.drawable.error)
            }
            .build()
    )

    Row(

        modifier = Modifier
            .clickable { clickedItem.invoke(coin.id) }
            .height(IntrinsicSize.Min)
            .sizeIn(minHeight = 72.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 12.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Image(
            painter = painter,
            contentDescription = coin.CoinName,
            modifier = Modifier
                .size(48.dp)
                .drawBehind {
                    drawCircle(
                        color = Color.Gray,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 1.dp.toPx()) // Çizgi kalınlığı
                    )
                }
                .padding(6.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.CenterStart
        )


        Column(
            modifier = Modifier.fillMaxHeight().weight(1f).padding(start = 10.dp),
            verticalArrangement = Arrangement.Center,

            ) {

            Text(
                modifier = Modifier.padding(bottom = 6.dp),
                text = coin.coinSymbol.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )

            if (!coin.coinSymbol.equals(coin.CoinName, ignoreCase = true)) {
                Text(
                    text = coin.CoinName,
                    fontSize = 10.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
        }



        Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.End) {


            Text(
                text = "$${coin.CoinPrice}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface
            )

            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically) {

                Image(
                    modifier = Modifier
                        .size(height = 7.dp, width = 10.dp)
                        .rotate(
                            if (coin.CoinChangePercente.contains("+")) 0.0f
                            else 180.0f
                        ).padding(end = 2.dp),
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = stringResource(id = R.string.change24),
                    colorFilter = ColorFilter.tint(
                        if (coin.CoinChangePercente.contains("+"))
                            Color(0xFF40AE95) else Color(0xFFF44336)
                    ),
                    contentScale = ContentScale.FillBounds,
                    alignment = Alignment.BottomStart
                )

                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = coin.CoinChangePercente.substring(
                        1, coin.CoinChangePercente.length - 1) + "%",
                    fontSize = 14.sp,
                    color = if (coin.CoinChangePercente.contains("+"))
                        Color(0xFF40AE95) else Color(0xFFF44336)
                )
            }


        }

    }
}

@Preview
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
