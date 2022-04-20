package com.finance.trade_learn.clickListener

import android.view.View
import androidx.navigation.Navigation
import com.finance.trade_learn.view.HomeDirections

class HomePageClickListener ( ):ListenerInterface {
    override fun clickListener(view: View) {
        val directions = HomeDirections.actionHomeToTradePage()
        Navigation.findNavController(view).navigate(directions)
    }
}