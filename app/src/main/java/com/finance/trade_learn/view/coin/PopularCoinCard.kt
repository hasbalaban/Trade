package com.finance.trade_learn.view.coin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .clickable { clickedItem.invoke(coin.CoinName) }
            .height(110.dp)
            .padding(horizontal = 6.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Image(
                    painter = rememberAsyncImagePainter(coin.CoinImage),
                    contentDescription = coin.CoinName,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = coin.CoinChangePercente,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (coin.CoinChangePercente.contains("+"))
                        Color(0xFF0BB600) else Color(0xFF2ebd85),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(end = 8.dp),

                ) {
                Text(
                    text = coin.CoinName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = coin.coinSymbol.uppercase(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){

                    Text(
                        text = coin.CoinPrice.let { "$${it}" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Image(
                        painter = painterResource(id = R.drawable.arrow_outward),
                        contentDescription = "price raised",
                        colorFilter = ColorFilter.tint(
                            if (coin.CoinChangePercente.contains("+"))
                                Color(0xFF2ebd85) else Color(0xFFFF0000)
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PopularCoinScreenPreview() {
    MaterialTheme {

        val coinItem = CoinsHome(
            CoinName = "FLUX / USD",
            coinSymbol = "FLUX / USD",
            CoinPrice = "0.636005",
            CoinChangePercente = "+1.10",
            CoinImage = "https://coin-images.coingecko.com/coins/images/5163/large/Flux_symbol_blue-white.png?1696505679",
            raise = enumPriceChange.notr,
            marketCap = "221975378",
            total_volume = "221975378"
        )
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {

            Column(modifier = Modifier
                .padding(20.dp)
                .width(160.dp)
                .fillMaxWidth()
                .height(110.dp)
                .background(MaterialTheme.colorScheme.background)) {
                PopularCoinCard(coinItem, modifier = Modifier.weight(0.33f)) {}
            }
        }


    }
}


