package com.finance.trade_learn.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory

@Composable
fun WalletItemComposeView(historyList: List<NewModelForItemHistory>, function: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth().background(Color.Yellow)){
        items(historyList){item ->
            val painter = rememberAsyncImagePainter(model = item.Image, filterQuality = FilterQuality.High,)

            Row(modifier = Modifier.fillMaxWidth().clickable {
                function(item.CoinName)
            }) {
                Image(painter = painter,
                    modifier = Modifier.weight(1f),
                    contentDescription = null
                )
                Text(modifier =  Modifier.weight(2f),
                    text = item.CoinName,
                    fontSize = 15.sp
                )

                Text(modifier = Modifier.weight(3f),
                    fontSize = 15.sp,
                    text = item.CoinAmount)

                Text(modifier = Modifier.weight(2f),
                    fontSize = 15.sp,
                    text = item.Total)
            }

        }
    }
}