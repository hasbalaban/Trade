package com.finance.trade_learn.utils

import android.content.Context
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView


object Ads{
    fun listenerAdRequest (adView : AdView,adFragmentName : String,context: Context): AdListener {

        val listener = object  : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = View.VISIBLE
                sharedPreferencesManager(context).addSharedPreferencesLong(adFragmentName,System.currentTimeMillis()+(28*1000))
                super.onAdLoaded()
            }

        }
        return listener
    }

}