package com.finance.trade_learn.view.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.enumPriceChange
import com.finance.trade_learn.models.modelsConvector.CoinsHome
import com.finance.trade_learn.models.modelsConvector.Percent
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.utils.percenteChange
import com.finance.trade_learn.view.LocalHomeViewModel
import com.finance.trade_learn.view.coin.CoinItemScreen
import com.finance.trade_learn.view.coin.ItemIcon
import com.finance.trade_learn.view.wallet.format
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun HomeScreen(
    openTradePage: (String) -> Unit,
    clickedViewAll: () -> Unit,
    openMarketPage: () -> Unit
) {
    val viewModel = LocalHomeViewModel.current

    val islogin by BaseViewModel.isLogin.collectAsState()
    val marketItems = BaseViewModel.allCryptoItems.collectAsState()


    LaunchedEffect(BaseViewModel) {
        if (!islogin) {
            delay(3000)
            viewModel.getMyCoinsDetails()
        }
    }

    if (islogin) {
        val userInfo = BaseViewModel.userInfo.collectAsState()
        viewModel.getDataFromApi(userInfo.value.data?.balances?.map { it.itemName })
    }

    if (marketItems.value.isNotEmpty()) {
        StockitPortfolioScreen(
            openTradePage = openTradePage,
            clickedViewAll = clickedViewAll,
            openMarketPage = openMarketPage
        )
    }


}

@Composable
fun StockitPortfolioScreen(
    openTradePage: (String) -> Unit,
    clickedViewAll: () -> Unit,
    openMarketPage: () -> Unit
) {
    val viewModel = LocalHomeViewModel.current
    val items by viewModel.myCoinsNewModel.observeAsState(emptyList())
    val isLogin by BaseViewModel.isLogin.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Welcome",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colors.onPrimary
            )

            Spacer(modifier = Modifier.width(12.dp))

            BaseViewModel.userInfo.value.data?.let {
                Text(
                    text = "-   " + it.nameAndSurname,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))


        BalanceCard(clickedViewAll = clickedViewAll)


        Spacer(modifier = Modifier.height(16.dp))


        // Portfolio Section

        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Portfolio",
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    text = "View all",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color(0xff3E84F6)
                    ),
                    color = Color(0xff3E84F6),
                    modifier = Modifier.clickable {
                        clickedViewAll.invoke()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(items) {
                        PortfolioCard(
                            itemName = it.CoinName,
                            modifier = Modifier
                                .clickable {
                                    openTradePage.invoke(it.CoinName)
                                }
                                .sizeIn(minWidth = 220.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        if (isLogin){
            WatchListSection(openTradePage = openTradePage, openMarketPage = openMarketPage)
        }

    }
}

@Composable
fun PortfolioCard(
    itemName: String,
    modifier: Modifier,
) {
    val item = BaseViewModel.allCryptoItems.value.firstOrNull {
        itemName.lowercase(Locale.getDefault()) == it.id.lowercase(Locale.getDefault())
    } ?: return

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                ItemIcon(
                    imageUrl = item.image,
                    itemName = item.name,
                    modifier = Modifier.size(48.dp)
                )



                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Portfolio",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.9f)
                    )
                    val currentPrice = when {
                        (item.current_price ?: 0.0) > 1.0 -> item.current_price?.format(2)
                        (item.current_price ?: 0.0) > 0.001 -> item.current_price?.format(3)
                        (item.current_price ?: 0.0) > 0.0001 -> item.current_price?.format(4)
                        (item.current_price ?: 0.0) > 0.00001 -> item.current_price?.format(5)
                        (item.current_price ?: 0.0) > 0.000001 -> item.current_price?.format(6)
                        (item.current_price ?: 0.0) > 0.0000001 -> item.current_price?.format(7)
                        (item.current_price ?: 0.0) > 0.00000001 -> item.current_price?.format(8)
                        (item.current_price ?: 0.0) > 0.000000001 -> item.current_price?.format(9)
                        (item.current_price ?: 0.0) > 0.0000000001 -> item.current_price?.format(10)
                        else -> item.current_price?.format(11)
                    }

                    Text(
                        text = currentPrice ?: "",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                val priceChangePercent = item.price_change_percentage_24h
                val priceChangeColor =
                    if ((priceChangePercent ?: 0.0) > 0.0) Color(0xFF4CAF50) else Color(0xFFF44336)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(height = 7.dp, width = 12.dp)
                            .rotate(
                                if ((priceChangePercent ?: 0.0) > 0.0) 0.0f else 180f
                            )
                            .padding(end = 2.dp),
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = stringResource(id = R.string.change24),
                        colorFilter = ColorFilter.tint(priceChangeColor),
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.BottomStart
                    )

                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = priceChangePercent?.format(2).toString() + "%",
                        style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                        color = priceChangeColor,
                        textAlign = TextAlign.End
                    )
                }

            }
        }
    }
}


@Composable
private fun BalanceCard(clickedViewAll: () -> Unit) {
    val viewModel = LocalHomeViewModel.current
    val cryptoItems by viewModel.myCoinsNewModel.observeAsState(emptyList())
    val totalBalance by viewModel.totalBalance.collectAsState(0f)

    val totalDollarBalance =
        cryptoItems?.firstOrNull { it.CoinName.contains("tether", true) }?.CoinAmount
            ?: 0.0

    val animatedBalance by animateFloatAsState(
        targetValue = totalBalance,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ), label = ""
    )


    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF1E88E5), // Blue background color
        elevation = 4.dp,
        modifier = Modifier
            .clickable {
                clickedViewAll.invoke()
            }
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding inside the card
            verticalArrangement = Arrangement.Center, // Centers content vertically
            horizontalAlignment = Alignment.CenterHorizontally // Centers content horizontally
        ) {
            Text(
                text = "Total Coin Value",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "\$${animatedBalance.toDouble().format(2)}",
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Dollar Holdings",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "\$${totalDollarBalance.format(2)}",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                )
            )
        }
    }

}

@Composable
private fun WatchListSection(openTradePage: (String) -> Unit, openMarketPage: () -> Unit) {
    val userInfo = BaseViewModel.userInfo.collectAsState()

    val items = userInfo.value.data?.userWatchList?.map { watchListItem ->
        val currentItemInfo =
            BaseViewModel.allCryptoItems.value.firstOrNull { it.id == watchListItem.itemId }
        if (currentItemInfo == null) null
        else {

            val percenteChange: Percent? = if (currentItemInfo.price_change_24h == null) {
                Percent(0.0, "+", "%")
            } else {
                percenteChange(currentItemInfo.price_change_percentage_24h.toString())
            }

            val coinPercenteChange = percenteChange?.raise + (percenteChange?.percentChange
                .toString() + "0000").subSequence(0, 4).toString() + "%"

            CoinsHome(
                id = currentItemInfo.id,
                CoinName = currentItemInfo.name.uppercase(Locale.getDefault()) + " / USD",
                coinSymbol = currentItemInfo.symbol.uppercase(Locale.getDefault()) + " / USD",
                CoinPrice = (currentItemInfo.current_price.toString() + "00000000").subSequence(
                    0,
                    8
                ).toString(),
                CoinChangePercente = coinPercenteChange,
                CoinImage = currentItemInfo.image,
                raise = if ((currentItemInfo.price_change_percentage_24h
                        ?: 0.0) > 0.0
                ) enumPriceChange.pozitive else enumPriceChange.negative,
                marketCap = currentItemInfo.market_cap,
                total_volume = currentItemInfo.total_volume,
            )
        }
    }?.mapNotNull { it }

    Text(
        text = "Watchlist",
        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colors.onPrimary
    )

    if (items.isNullOrEmpty()) {
        EmptyWatchlist(openMarketPage = openMarketPage)
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier) {
                items(
                    items = items,
                    key = {
                        it.CoinName
                    }
                ) {
                    CoinItemScreen(it) { selectedItemName ->
                        openTradePage.invoke(selectedItemName)
                    }
                }
            }
        }
    }


}

@Composable
fun EmptyWatchlist(openMarketPage: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Item",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xff3E84F6).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .clickable { openMarketPage.invoke() }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.watchlist_empty),
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.click_to_add_something),
                style = MaterialTheme.typography.body2.copy(fontSize = 16.sp),
                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    FinanceAppTheme {
        HomeScreen(openTradePage = {}, clickedViewAll = {}, openMarketPage = {})
    }
}