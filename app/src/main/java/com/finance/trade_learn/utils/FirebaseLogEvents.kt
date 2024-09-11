package com.finance.trade_learn.utils

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object FirebaseLogEvents {
    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }




    fun logEvent(event : String){
        firebaseAnalytics.logEvent(event){
            param(event, event)
        }
    }

    fun logSignUpEvent(bundle: Bundle){
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP){
            param("userInfo", bundle)
        }
    }

    fun logLoginEvent(bundle: Bundle){
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN){
            param("userInfo", bundle)
        }
    }

    fun logClickFilterEvent(bundle: Bundle) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
            param("filter", bundle)
        }
    }


}