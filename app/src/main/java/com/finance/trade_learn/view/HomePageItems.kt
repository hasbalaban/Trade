package com.finance.trade_learn.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.view.coin.CoinItemScreen
import java.util.Locale

@Composable
fun HomePageItems (
    coinsHome: List<CoinsHome>?,
    onViewClick : (String) -> Unit
) {
    coinsHome?.let {item ->

        LazyColumn(modifier = Modifier){
            items(
                items = item,
                key = {
                    it.CoinName
                }
            ){
                CoinItemScreen(it){ selectedItemName ->
                    onViewClick.invoke(selectedItemName)
                }

            }
        }
    }
}


@Composable
fun HomePageItem(coinsHome: CoinsHome, clickedItem: (String) -> Unit) {

    val color1 = when (coinsHome.CoinChangePercente.subSequence(0, 1)) {
        "-" -> Color(0xffF6465D)
        "+" -> Color(0xff2ebd85)
        else -> Color.Black

    }

    val color2 = when (coinsHome.CoinChangePercente.subSequence(0, 1)) {
        "-" -> Color.White
        "+" -> Color.White
        else -> Color.Black
    }

    val background = when (coinsHome.CoinChangePercente.subSequence(0, 1)) {
        "-"-> Color(0xff2ebd85)
        "+" -> Color(0xffF6465D)
        else -> Color.Transparent
    }

    Row(modifier = Modifier
        .height(60.dp)
        .fillMaxWidth()
        .padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .clickable {

                val coinName = solveCoinName(coinsHome.CoinName)
                clickedItem.invoke(coinName.lowercase(Locale.getDefault()))
            }

        ) {
            val (nameAndImageView, priceAndChangeView) = createRefs()

            Row(modifier = Modifier
                .constrainAs(nameAndImageView) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints

                }) {
                Text(
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(top = 8.dp, end = 6.dp),
                    text = coinsHome.coinSymbol
                )

                val painter =
                    rememberAsyncImagePainter(model = coinsHome.CoinImage,
                        filterQuality = FilterQuality.High,
                        )

                Image(
                    painter = painter,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                    ,
                    contentDescription = null
                )

            }


            Row(modifier = Modifier.constrainAs(priceAndChangeView){
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }) {
                Text(
                    modifier = Modifier.padding(end = 12.dp, top = 2.dp),
                    text = coinsHome.CoinPrice,
                    color = color1,
                )
                Text(
                    modifier = Modifier
                        .background(
                            color = background,
                            shape = RoundedCornerShape(size = 6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                    ,
                    text = coinsHome.CoinChangePercente,
                    color = color2,
                )
            }
        }
    }
}

@Preview
@Composable
fun APreview (){
    Surface(modifier = Modifier.background(Color.White)) {}
}