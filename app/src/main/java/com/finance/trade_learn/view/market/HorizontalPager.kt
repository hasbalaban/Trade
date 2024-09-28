package com.finance.trade_learn.view.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finance.trade_learn.view.market.currenciesScreen.CurrenciesScreen
import kotlinx.coroutines.launch


val tabList = listOf("Coin", "Currencies")

@Composable
fun HorizontalPagerScreen(
    openTradePage: (String) -> Unit,
    navigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        tabList.size
    }

    Column(modifier = Modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){


        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            //edgePadding = 12.dp,
            backgroundColor = MaterialTheme.colors.primary,
            indicator = {tabPositions ->
                HorizontalDivider(
                    Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color(0xFF1E88E5),
                    thickness = 2.dp

                )

            }
        ) {
            tabList.forEachIndexed { index, s ->

                Row(modifier = Modifier.height(IntrinsicSize.Min)){

                    Tab(
                        selected = pagerState.currentPage == index,
                        text = {
                            when(index){
                                0 ->{
                                    Text(
                                        text = "Coin", color = MaterialTheme.colors.onPrimary,
                                        fontSize = 24.sp
                                    )
                                }
                                1 ->{
                                    Text(
                                        text = "Currencies", color = MaterialTheme.colors.onPrimary,
                                        fontSize = 24.sp, style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                                    )
                                }

                            }


                        },
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )

                    VerticalDivider(
                        Modifier.padding(paddingValues = PaddingValues(12.dp)),
                        color = Color.White.copy(alpha = 0.3f))

                }

            }

        }



        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { index ->
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                when (index) {
                    0 -> {
                        MarketScreen(
                            openTradePage = openTradePage,
                            navigateToLogin = navigateToLogin
                        )
                    }
                    1 -> {
                        CurrenciesScreen()
                    }

                    else -> {
                        Text(
                            text = "Other...",
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 24.sp
                        )
                    }
                }
            }

        }
    }


}