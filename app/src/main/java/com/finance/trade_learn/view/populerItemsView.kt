package com.finance.trade_learn.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.Adapters.solveCoinName
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.viewModel.ViewModeHomePage
import java.util.*


@Composable
fun PopularItemsView (
    viewModel : ViewModeHomePage = androidx.lifecycle.viewmodel.compose.viewModel(),
    clickedItem: (String) -> Unit
){
    val list = viewModel.listOfCryptoForPopular.observeAsState().value
    list?.let {

        val first = list.getOrNull(0)
        val second = list.getOrNull(1)
        val third = list.getOrNull(2)

        Row(modifier = Modifier.fillMaxWidth()){
            first?.let {
                Column (modifier = Modifier
                    .clickable {
                        val coinName = solveCoinName(it.CoinName)
                        clickedItem.invoke(coinName.lowercase(Locale.getDefault()))
                    }
                    .padding(start = 6.dp)
                    .fillMaxWidth(0.33f)){
                    PopularItems(first)
                }
            }
            second?.let {
                Column (modifier = Modifier
                    .clickable {
                        val coinName = solveCoinName(it.CoinName)
                        clickedItem.invoke(coinName.lowercase(Locale.getDefault()))
                    }
                    .padding(start = 6.dp)
                    .fillMaxWidth(0.5f)){
                    PopularItems(second)
                }
            }
            third?.let {
                Column (modifier = Modifier
                    .clickable {
                        val coinName = solveCoinName(it.CoinName)
                        clickedItem.invoke(coinName.lowercase(Locale.getDefault()))
                    }
                    .padding(start = 6.dp)
                    .fillMaxWidth(1f)){
                    PopularItems(third)
                }
            }
        }

    }


}

@Composable
fun PopularItems (coinsHome : CoinsHome){

    val color1 = when (coinsHome.CoinChangePercente.subSequence(0, 1)) {
        "-" -> Color(0xffF6465D)
        "+" -> Color(0xff2ebd85)
        else -> Color(0xff000000)

    }

    val color2 = when (coinsHome.raise) {
        enumPriceChange.pozitive -> Color(0xff2ebd85)
        enumPriceChange.negative -> Color(0xffF6465D)
        enumPriceChange.notr -> Color(0xff000000)
    }

    Column(){
        Row(){
            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xffECDBA5),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(3.dp, 5.dp)
                ,
                text = coinsHome.coinSymbol,
                color = Color.Black,
                fontSize = 13.sp,
            )
            Text(
                modifier = Modifier.padding(3.dp),
                fontSize = 11.sp,
                color = color1,
                text = coinsHome.CoinChangePercente)
        }
        Text(
            modifier = Modifier.padding(top = 10.dp, bottom = 2.dp, start = 2.dp, end = 2.dp),
            fontSize = 15.sp,
            color = color2,
            text = coinsHome.CoinPrice)

    }

}