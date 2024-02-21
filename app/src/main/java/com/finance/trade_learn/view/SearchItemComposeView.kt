package com.finance.trade_learn.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.models.coin_gecko.CoinInfoList

@Composable
fun SearchItemComposeView(CoinInfo: CoinInfoList, clickedItem: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable {
                clickedItem(CoinInfo.id)
            }
            .height(60.dp)
            .fillMaxWidth()
            .padding(top = 6.dp, start = 6.dp, end = 6.dp)

    ) {
        Text(
            color = MaterialTheme.colors.onPrimary, text = CoinInfo.name, fontSize = 16.sp
        )
        Image(painter = painterResource(id = R.drawable.favorite_close), contentDescription = null)
    }
}