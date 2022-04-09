package com.finance.trade_learn.utils

import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView


object Ads{


    fun listenerAdRequest (adView : AdView): AdListener {

        val listener = object  : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = View.VISIBLE
                super.onAdLoaded()
            }

        }
        return listener
    }

}