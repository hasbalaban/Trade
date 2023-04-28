package com.finance.trade_learn.ads_manager

import android.os.Build
import android.os.Debug
import com.finance.trade_learn.BuildConfig

object AdsConst{
     val shouldShowAds = BuildConfig.DEBUG.not()
}