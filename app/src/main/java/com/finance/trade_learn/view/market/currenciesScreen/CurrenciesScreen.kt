package com.finance.trade_learn.view.market.currenciesScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.database.dataBaseEntities.toMap
import com.finance.trade_learn.view.home.PortfolioCard1
import com.finance.trade_learn.viewModel.CurrenciesViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.utils.FirebaseLogEvents

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrenciesScreen(viewModel: CurrenciesViewModel = hiltViewModel()) {
    val items by BaseViewModel.currencies.collectAsState()
    val mappedList = items.toMap()

    LazyColumn(modifier = Modifier.fillMaxWidth()
    ) {
        stickyHeader {

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary)
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(stringResource(id = R.string.Symbol_text), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp), overflow = TextOverflow.Ellipsis)
                    Text(stringResource(id = R.string.buy).replace(":", ""), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(stringResource(id = R.string.sell), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(stringResource(id = R.string.change).replace(": ", ""), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Spacer(modifier = Modifier.width(24.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))

            }
        }

        items(mappedList){


            PortfolioCard1(
                isCurrency = true,
                it,
                modifier = Modifier
                    .clickable {
                        //openTradePage.invoke(item.CoinName)
                    }
                    .padding(vertical = 6.dp)
            )
        }

    }
}


@Composable
fun CurrenciesForHomeScreen(){
    var isExpanded by remember { mutableStateOf(false) }

    val items by BaseViewModel.currencies.collectAsState()
    val adjustedList = items.toMap().chunked(
        if (isExpanded) 2 else 1
    )

    Column {

        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){

            Text(
                text = stringResource(id = R.string.currencies),
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.onPrimary
            )


            Text(
                text = if(isExpanded) stringResource(id = R.string.less) else stringResource(id = R.string.expand),
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color(0xff3E84F6)
                ),
                color = Color(0xff3E84F6),
                modifier = Modifier.clickable {
                    isExpanded = !isExpanded
                    FirebaseLogEvents.logEvent("$isExpanded clicked")
                }
            )
        }



        LazyRow {
            items(adjustedList){
                Column(
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .height(IntrinsicSize.Min),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    val firstItem = it.firstOrNull()
                    val secondItem = it.getOrNull(1)

                    firstItem?.let {
                        StockCard(
                            title = it.CoinName,
                            price = it.currentPrice,
                            changePercent = it.percentChange ?: "",
                            percentColor = if (it.percentChange?.contains("+") == true)
                                Color(0xFF40AE95) else Color(0xFFF44336),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                    }
                    secondItem?.let {
                        StockCard(
                            title = it.CoinName,
                            price = it.currentPrice,
                            changePercent = it.percentChange ?: "",
                            percentColor = if (it.percentChange?.contains("+") == true)
                                Color(0xFF40AE95) else Color(0xFFF44336),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        )
                    }
                }
            }
        }

    }


}







@Composable
fun StockCard(
    title: String,
    price: String,
    changePercent: String,
    percentColor: Color,
    modifier: Modifier
) {
    Column(
        modifier = modifier.padding(4.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF001F6B),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEBEBEB))
                .padding(start = 4.dp, top = 4.dp, end = 2.dp, bottom = 4.dp),
            textAlign = TextAlign.Start
        )

        Column(
            modifier = Modifier
                .background(color = Color(0xFF001F6B),)
                .padding(start = 6.dp, end = 4.dp)
        ){
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.White
            )


            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){

                Text(
                    text = changePercent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = percentColor
                )



                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(
                            if (changePercent.contains("+")) -90f
                            else 0f
                        )
                        .background(
                            if (changePercent.contains("+"))
                                Color(0xFF40AE95) else Color(0xFFF44336),
                            shape = TriangleShape
                        )
                        .padding(4.dp)
                )

            }

        }}
}




@Preview
@Composable
fun Preview(){

    Row {

        StockCard(
            title = "CoinName",
            price = "9109.34",
            changePercent = "2.37%",
            percentColor = Color(0xFF00FF00),
            modifier = Modifier.weight(1f)
        )


        StockCard(
            title = "CoinName",
            price = "9109.34",
            changePercent = "2.37%",
            percentColor = Color(0xFF00FF00),
            modifier = Modifier.weight(1f)
        )

        StockCard(
            title = "CoinName",
            price = "9109.34",
            changePercent = "2.37%",
            percentColor = Color(0xFF00FF00),
            modifier = Modifier.weight(1f)
        )
        StockCard(
            title = "CoinName",
            price = "9109.34",
            changePercent = "2.37%",
            percentColor = Color(0xFF00FF00),
            modifier = Modifier.weight(1f)
        )
        StockCard(
            title = "CoinName",
            price = "9109.34",
            changePercent = "2.37%",
            percentColor = Color(0xFF00FF00),
            modifier = Modifier.weight(1f)
        )
    }
}


// Üçgen şekil tanımlama
val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width, 0f) // Sağ üst köşe
    lineTo(size.width, size.height) // Sağ alt köşe
    lineTo(0f, size.height) // Sol alt köşe
    close() // Başlangıç noktasına dön
}