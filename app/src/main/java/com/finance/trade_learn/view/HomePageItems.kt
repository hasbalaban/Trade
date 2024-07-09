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

@Preview
@Composable
fun APreview (){
    Surface(modifier = Modifier.background(Color.White)) {}
}