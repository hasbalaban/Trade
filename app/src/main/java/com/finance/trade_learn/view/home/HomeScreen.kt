package com.finance.trade_learn.view.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.view.LocalHomeViewModel
import com.finance.trade_learn.view.coin.ItemIcon
import com.finance.trade_learn.view.wallet.format

@Composable
fun HomeScreen(openTradePage: (String) -> Unit, clickedViewAll : () -> Unit) {
    val viewModel = LocalHomeViewModel.current

    LaunchedEffect(Unit) {
        if (!BaseViewModel.isLogin.value) {
            viewModel.getMyCoinsDetails()
        }
    }

    if (BaseViewModel.isLogin.value) {
        val userInfo = BaseViewModel.userInfo.collectAsState()
        if (BaseViewModel.isLogin.value) {
            viewModel.getDataFromApi(userInfo.value.data?.balances?.map { it.itemName })
        }
    }


    StockitPortfolioScreen(
        openTradePage = openTradePage,
        clickedViewAll = clickedViewAll
    )
}

@Composable
fun StockitPortfolioScreen(openTradePage: (String) -> Unit, clickedViewAll: () -> Unit) {
    // Background color for the screen
    val backgroundColor = Color(0xFFF5F6FA) // Light grayish background color
    val textColor = Color(0xFF000000) // Black text color
    val cardBackgroundColor = Color.White
    val portfolioTitleColor = Color(0xFFB0B0B0) // Gray text for portfolio titles
    val negativeChangeColor = Color(0xFFE74C3C) // Red color for negative changes
    val positiveChangeColor = Color(0xFF2ECC71) // Green color for positive changes


    val viewModel = LocalHomeViewModel.current
    val items by viewModel.myCoinsNewModel.observeAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .padding(vertical = 16.dp, horizontal = 12.dp)
    ) {
        BaseViewModel.userInfo.value.data?.let {
            Text(
                text = it.nameAndSurname,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Normal),
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome to Stockit",
            style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))


        BalanceCard()


        Spacer(modifier = Modifier.height(16.dp))


        // Portfolio Section

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            Text(
                text = "View all",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Color(0xff3E84F6)),
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
                        item = it,
                        portfolioValue = "131,46",
                        changePercentage = "2,02%",
                        changeColor = negativeChangeColor,
                        modifier = Modifier.clickable {
                            openTradePage.invoke(it.CoinName)
                        }.sizeIn(minWidth = 220.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Watchlist",
            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

@Composable
fun PortfolioCard(
    item: NewModelForItemHistory,
    portfolioValue: String,
    changePercentage: String,
    changeColor: Color,
    modifier: Modifier,
) {
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
                    imageUrl = item.Image,
                    itemName = item.CoinName,
                    modifier = Modifier.size(48.dp)
                )



                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = item.CoinName,
                        style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = item.CoinName,
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
                    Text(
                        text = portfolioValue,
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.onPrimary
                    )
                }



                Text(
                    modifier = Modifier.padding(6.dp),
                    text = changePercentage,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
                    color = changeColor,
                    textAlign = TextAlign.End
                )

            }
        }
    }
}


@Composable
private fun BalanceCard() {
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
            .fillMaxWidth()
            .height(200.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\$${animatedBalance.toDouble().format(2)}",
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Dollar Holdings",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

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

@Preview
@Composable
fun HomeScreenPreview() {
    FinanceAppTheme {
        HomeScreen(openTradePage = {}, clickedViewAll = {})
    }
}