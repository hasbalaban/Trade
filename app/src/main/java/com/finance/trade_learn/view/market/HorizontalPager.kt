package com.finance.trade_learn.view.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch


val tabList = listOf("Coin", "Currencies", "Other")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerScreen(openTradePage: (String) -> Unit, navigateToLogin: () -> Unit) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState {
        tabList.size
    }

    Column(modifier = Modifier.padding(top = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 12.dp,
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
                                        fontSize = 24.sp
                                    )
                                }
                                else ->{
                                    Text(
                                        text = "Other", color = MaterialTheme.colors.onPrimary,
                                        fontSize = 24.sp
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
                        Modifier.padding(vertical = 12.dp),
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
                        Text(
                            text = "Currencies...",
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 24.sp
                        )
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