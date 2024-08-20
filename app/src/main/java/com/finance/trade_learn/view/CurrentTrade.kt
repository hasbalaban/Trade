package com.finance.trade_learn.view

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.finance.trade_learn.utils.Constants
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


enum class CoinProgress (){ SUM,MINUS }

@Composable
private fun reviewUs(){
    //val reviewUs : ReviewUsI = this
 //   val reviewUsResult =  reviewUs.reviewUsRequestCompleteListener(activity = requireActivity(), context = requireContext())
  //  reviewUs.reviewUsStart(activity = requireActivity(), manager = reviewUsResult.first, reviewInfo = reviewUsResult.second)
}

@Composable
private fun ShowInterstitialAd(activity: Activity) {
    adInterstitial?.apply {
        show(activity)
        adInterstitial = null
        SharedPreferencesManager(LocalContext.current).addSharedPreferencesLong("interstitialAdLoadedTime",System.currentTimeMillis()+(60*60*1000))
    }
}

private fun setInterstitialAd(context: Context) {
    if (Constants.SHOULD_SHOW_ADS.not()) return
    val currentMillis = System.currentTimeMillis()
    val updateTime = SharedPreferencesManager(context).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
    if (currentMillis < updateTime) return

    val adRequest = AdRequest.Builder().build()
    MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())
    InterstitialAd.load(context, "ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {}
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            adInterstitial = interstitialAd
        }
    })
}

private var adInterstitial: InterstitialAd? = null