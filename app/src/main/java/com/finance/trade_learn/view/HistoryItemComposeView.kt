package com.finance.trade_learn.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.database.dataBaseEntities.SaveCoin
import com.finance.trade_learn.enums.TradeType
import java.text.DecimalFormat

@Composable
fun HistoryItemComposeView(saveCoins : ArrayList<SaveCoin>) {
    LazyColumn(){
        items(saveCoins){item ->
            Column() {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(55.dp)
                        .padding(top = 1.dp)
                ) {
                    var totalColor by remember { mutableStateOf(R.color.negative) }
                    var statusColor by remember { mutableStateOf(R.color.pozitive) }
                    if (item.tradeOperation== TradeType.Sell.toString()){
                        totalColor = R.color.pozitive
                        statusColor = R.color.negative
                    }
                    val df = DecimalFormat("#.######")
                    val coinAmount by remember { mutableStateOf(df.format(item.coinAmount.toDouble())) }
                    HistoryItem(modifier = Modifier.weight(1f), text = item.coinName, textColor = Color.Unspecified)
                    HistoryItem(modifier = Modifier.weight(1f), text = coinAmount, textColor = Color.Unspecified)
                    HistoryItem(modifier = Modifier.weight(1f), text = item.coinPrice, textColor = Color.Unspecified)
                    HistoryItem(modifier = Modifier.weight(1f), text = item.total, textColor = colorResource(id = totalColor))
                    HistoryItem(modifier = Modifier.weight(1f), text = item.date, textColor = Color.Unspecified)
                    HistoryItem(modifier = Modifier.weight(1f), text = item.tradeOperation, textColor = colorResource(id = statusColor))
                }
                
                Row(modifier = Modifier
                    .fillMaxSize()
                    .background(color = colorResource(id = R.color.hint_grey))
                    .height(1.dp)) {}
            }
        }
    }

}

@Composable
fun HistoryItem(modifier: Modifier, text: String, textColor: Color = Color.Unspecified ){
    Text(modifier = modifier.padding(top = 5.dp), text = text, fontSize = 14.sp, color = textColor, textAlign = TextAlign.Center)
}