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
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.base.BaseViewModel.Companion.allCryptoItems
import com.finance.trade_learn.base.BaseViewModel.Companion.currentItems
import com.finance.trade_learn.database.dataBaseEntities.toMap
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.models.watchList.toCoinHome
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.utils.FirebaseLogEvents
import com.finance.trade_learn.view.LocalHomeViewModel
import com.finance.trade_learn.view.coin.CoinItemScreen
import com.finance.trade_learn.view.coin.ItemIcon
import com.finance.trade_learn.view.market.currenciesScreen.StockCard
import com.finance.trade_learn.view.wallet.format
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    openTradePage: (String) -> Unit,
    clickedViewAll: () -> Unit,
    openMarketPage: () -> Unit,
    navigateToLogin : () -> Unit,
    navigateToSignUp : () -> Unit,
) {
    val viewModel = LocalHomeViewModel.current

    val islogin by BaseViewModel.isLogin.collectAsState()


    val marketItems = currentItems.collectAsState()

    if (!islogin && marketItems.value.isNotEmpty()) {
        viewModel.getMyCoinsDetails()
    }
    if (islogin) {
        val userInfo = BaseViewModel.userInfo.collectAsState()
        viewModel.getDataFromApi(userInfo.value.data?.balances?.map { it.itemName })
    }



    if (marketItems.value.isNotEmpty()) {
        StockitPortfolioScreen(
            openTradePage = openTradePage,
            clickedViewAll = clickedViewAll,
            openMarketPage = openMarketPage,
            navigateToLogin = navigateToLogin,
            navigateToSignUp = navigateToSignUp
        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockitPortfolioScreen(
    openTradePage: (String) -> Unit,
    clickedViewAll: () -> Unit,
    openMarketPage: () -> Unit,
    navigateToLogin : () -> Unit,
    navigateToSignUp : () -> Unit,
) {
    val viewModel = LocalHomeViewModel.current
    val items by viewModel.myCoinsNewModel.observeAsState(emptyList())

    var isExpanded by remember { mutableStateOf(false) }
    val currenciesItems by BaseViewModel.currencies.collectAsState()
    val adjustedList = currenciesItems.toMap().chunked(
        if (isExpanded) 2 else 1
    )

    val isLogin by BaseViewModel.isLogin.collectAsState()
    val userInfo = BaseViewModel.userInfo.collectAsState()
    val watchlistItems by remember {
        derivedStateOf {
            userInfo.value.data?.userWatchList?.toCoinHome() ?: emptyList()
        }
    }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyToolbar(
                scrollBehavior = scrollBehavior
            )
        },
        backgroundColor = MaterialTheme.colors.primary
    ) { it ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

            BalanceCard(clickedViewAll = clickedViewAll)

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.portfolio_text),
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(text = stringResource(id = R.string.view_all),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color(0xff3E84F6)
                        ),
                        color = Color(0xff3E84F6),
                        modifier = Modifier.clickable {
                            FirebaseLogEvents.logEvent("click View All")
                            clickedViewAll.invoke()
                        })
                }

                Spacer(modifier = Modifier.height(8.dp))


                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        LazyRow(modifier = Modifier.padding(top = 4.dp)){
                            items(items){
                                PortfolioCard(portfolioItem = it, modifier = Modifier
                                    .clickable {
                                        openTradePage.invoke(it.CoinName)
                                    }
                                    .sizeIn(minWidth = 220.dp)
                                    .padding(start = 4.dp))
                            }
                        }
                    }

                    item {

                        Row (modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween){

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
                    }
                    item {
                        LazyRow {
                            items(adjustedList){
                                Column(
                                    modifier = Modifier.padding(top = 4.dp)
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

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            text = stringResource(id = R.string.watchlist_text),
                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colors.onPrimary
                        )
                    }

                    item {
                        WatchListSection(
                            openTradePage = openTradePage,
                            openMarketPage = openMarketPage,
                            navigateToLogin = navigateToLogin,
                            navigateToSignUp = navigateToSignUp
                        )
                    }


                    when {
                        !isLogin -> {
                            item {
                                LoginOrSignUpScreen(
                                    navigateToLogin = navigateToLogin,
                                    navigateToSignUp = navigateToSignUp
                                )
                            }
                        }

                        items.isNullOrEmpty() -> {
                            item {
                                EmptyWatchlist(openMarketPage)
                            }
                        }

                        else -> {

                            items(
                                items = watchlistItems,
                                key = { it.id }
                            ) {
                                CoinItemScreen(
                                    coin = it,
                                    navigateToLogin = navigateToLogin,
                                    clickedItem = { selectedItemName ->
                                        openTradePage.invoke(selectedItemName)
                                    }
                                )
                            }
                        }
                    }


                }
            }



        }



        }
    }


@Composable
fun PortfolioCard(
    portfolioItem: NewModelForItemHistory,
    modifier: Modifier
) {
    val item = allCryptoItems.value.firstOrNull {
        portfolioItem.CoinName.lowercase(Locale.getDefault()) == it.id.lowercase(Locale.getDefault())
    } ?: return

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                        text = item.name,
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.body2.copy(color = Color.Gray),
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(8.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.price),
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colors.onPrimary.copy(alpha = 0.9f)
                    )

                    Text(
                        text = portfolioItem.currentPrice,
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                }

                val priceChangePercent = item.price_change_percentage_24h
                val priceChangeColor = if ((priceChangePercent ?: 0.0) > 0.0) Color(0xFF4CAF50) else Color(0xFFF44336)

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
                        text = item.price_change_percentage_24h?.format(2) + "%",
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
fun PortfolioCard1(
    isCurrency : Boolean = false,
    portfolioItem: NewModelForItemHistory,
    modifier: Modifier,
) {
    val priceChangeColor = if (portfolioItem.percentChange?.contains("-") == false) Color(0xFF4CAF50) else Color(0xFFF44336)



    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {


            portfolioItem.percentChange?.let {
                Image(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(height = 10.dp, width = 16.dp)
                        .rotate(
                            if (!it.contains("-")) 0.0f else 180f
                        ),
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = stringResource(id = R.string.change24),
                    colorFilter = ColorFilter.tint(priceChangeColor),
                    contentScale = ContentScale.FillBounds,
                    alignment = Alignment.TopStart
                )
            }?: ItemIcon(
                imageUrl = portfolioItem.Image,
                itemName = portfolioItem.CoinName,
                modifier = Modifier.size(40.dp)
            )


            Text(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                text = portfolioItem.CoinName,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colors.onPrimary,
                overflow = TextOverflow.Ellipsis
            )


            val preText = if (isCurrency) "" else "$"
            Text(
                modifier = Modifier.weight(1f),
                text = preText + portfolioItem.CoinAmount.format(2),
                color = MaterialTheme.colors.onPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )

            Text(
                text = preText + portfolioItem.Total.toDouble().format(2),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )


            portfolioItem.percentChange?.let {
                Text(
                    text = portfolioItem.percentChange,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
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
                text = stringResource(id = R.string.total_coin_value),
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
                text = stringResource(id = R.string.dollar_holdings),
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
private fun WatchListSection(
    openTradePage: (String) -> Unit,
    openMarketPage: () -> Unit,
    navigateToLogin : () -> Unit,
    navigateToSignUp : () -> Unit,
) {



}



@Composable
fun LoginOrSignUpScreen(
    navigateToLogin: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.please_login_or_signup),
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = navigateToLogin,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.login))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = navigateToSignUp,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.sign_up))
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
                contentDescription = stringResource(id = R.string.add_watchlist),
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Color(0xff3E84F6).copy(alpha = 0.9f), shape = RoundedCornerShape(8.dp)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyToolbar(
    scrollBehavior: TopAppBarScrollBehavior
) {

    val userInfo = BaseViewModel.userInfo.collectAsState()

    val greetingMessage by remember { mutableIntStateOf(getTimeBasedGreeting()) }

    TopAppBar(
        title = {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = stringResource(id = greetingMessage),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colors.onPrimary
                )

                Spacer(modifier = Modifier.width(12.dp))

                userInfo.value.data?.let {
                    Text(
                        text = "-   " + it.nameAndSurname,
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
                        color = MaterialTheme.colors.onPrimary
                    )
                }

            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colors.primary,
            scrolledContainerColor = MaterialTheme.colors.primary
        ),
        scrollBehavior = scrollBehavior
    )




}


fun getTimeBasedGreeting(): Int {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 6..11 -> R.string.good_morning
        in 12..17 -> R.string.welcome
        in 18..23 -> R.string.good_evening
        else -> R.string.good_night
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    FinanceAppTheme {
        HomeScreen(openTradePage = {}, clickedViewAll = {}, openMarketPage = {}, navigateToLogin = {}, navigateToSignUp = {})
    }
}