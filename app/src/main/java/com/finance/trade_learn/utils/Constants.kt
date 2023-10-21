package com.finance.trade_learn.utils

import com.finance.trade_learn.R

object Constants {
    const val POST_NOTIFICATION = 1

    const val SHOULD_SHOW_ADS = false


    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            icon = R.drawable.home,
            route = "home"
        ),
        BottomNavItem(
            label = "Market",
            icon = R.drawable.market,
            route = "market"
        ),
        BottomNavItem(
            label = "Trade",
            icon = R.drawable.trade,
            route = "trade?coinName={coinName}"
        ),
        BottomNavItem(
            label = "Wallet",
            icon = R.drawable.wallet,
            route = "wallet"
        )
    )
}

sealed class Screens(val label: String? = null,val icon : Int, val route: String) {
    object Home : Screens("Home",R.drawable.home,"home" )
    object Market : Screens("market",R.drawable.home,"market" )
    class Trade(val item: String) : Screens("trade", R.drawable.home, "trade?coinName=$item")
    object Wallet : Screens("Wallet",R.drawable.home,"wallet")
    object SearchScreen : Screens("searchScreen",R.drawable.home,"searchScreen")
    object HistoryScreen : Screens("historyScreen",R.drawable.home,"historyScreen")
}



data class BottomNavItem(
    val label: String,
    val icon: Int,
    val route:String,
)