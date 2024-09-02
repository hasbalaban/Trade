package com.finance.trade_learn.view

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.view.coin.CoinItemScreen

@Composable
fun MarketPageItems (
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

@Preview
@Composable
fun APreview (){
    Surface(modifier = Modifier.background(Color.White)) {}
}