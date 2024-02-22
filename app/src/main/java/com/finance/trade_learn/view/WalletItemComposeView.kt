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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import java.text.DecimalFormat

@Composable
fun WalletItemComposeView(historyList: List<NewModelForItemHistory>, function: (String) -> Unit) {

    LazyColumn(modifier = Modifier.fillMaxWidth().background(color = Color(0xFFECD9D9))){
        itemsIndexed(historyList){index, item ->
            val painter = rememberAsyncImagePainter(model = item.Image, filterQuality = FilterQuality.High)

            val df = DecimalFormat("#.######")
            val coinAmount by remember { mutableStateOf(df.format(item.CoinAmount.toDouble())) }
            val coinTotal by remember { mutableStateOf(df.format(item.Total.toDouble())) }



            Row(modifier = Modifier.clickable {
                function.invoke(item.CoinName)
            }
                .background(color = if ((index % 2) == 0) Color(0xFFECD9D9) else Color.White )
                .fillMaxWidth()
                .padding(vertical = 6.dp)) {
                Image(painter = painter,
                    modifier = Modifier.weight(1f),
                    contentDescription = "coin image"
                )
                Text(modifier =  Modifier.weight(2f),
                    text = item.CoinName,
                    fontSize = 15.sp
                )

                Text(modifier = Modifier.weight(3f),
                    fontSize = 15.sp,
                    text = coinAmount)

                Text(modifier = Modifier.weight(2f),
                    fontSize = 15.sp,
                    text = coinTotal)
            }

        }
    }
}