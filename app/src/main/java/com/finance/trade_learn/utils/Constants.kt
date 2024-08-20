package com.finance.trade_learn.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.finance.trade_learn.R

object Constants {
    const val POST_NOTIFICATION = 1

    const val SHOULD_SHOW_ADS = false

    val BottomNavItems = listOf(
        BottomNavItem(
            label = R.string.home,
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            label = R.string.Market,
            icon = Icons.Filled.Search,
            route = "market"
        ),
        BottomNavItem(
            label = R.string.Trade,
            icon = Icons.Filled.SwapHoriz,
            route = "trade?coinName={coinName}"
        ),
        BottomNavItem(
            label = R.string.Wallet,
            icon = Icons.Filled.Wallet,
            route = "wallet"
        ),
        BottomNavItem(
            label = R.string.profile,
            icon = Icons.Filled.AccountCircle,
            route = "profile"
        )
    )
}

sealed class Screens(val label: String? = null,val icon : Int, val route: String) {
    object Home : Screens("Home",R.drawable.home,"home" )
    object Market : Screens("market",R.drawable.home,"market" )
    class Trade(val item: String) : Screens("trade", R.drawable.home, "trade?coinName=$item")
    object Wallet : Screens("Wallet",R.drawable.home,"wallet")
    object Profile : Screens("Profile",R.drawable.home,"profile")
    object Login : Screens("Login",R.drawable.home,"login")
    object ForgotPassword : Screens("forgot_password",R.drawable.home,"forgot_password")
    object VerificationCode : Screens("verification_code",R.drawable.home,"verification_code")
    object SingUp : Screens("Profile",R.drawable.home,"sign_up")
    object HistoryScreen : Screens("historyScreen",R.drawable.home,"historyScreen")
}



data class BottomNavItem(
    val label: Int,
    val icon: ImageVector,
    val route:String,
)