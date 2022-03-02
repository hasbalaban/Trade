package com.finance.trade_learn.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

// this interface as default will run when called to modifier override
interface ReviewUsI {
    fun reviewUsRequestCompleteListener(
        activity: Activity,
        context: Context
    ): Pair<ReviewManager, ReviewInfo?> {
        val manager = ReviewManagerFactory.create(context)
        val task = manager.requestReviewFlow()
        var reviewInfo: ReviewInfo? = null
        task.addOnCompleteListener { reviewTask ->

            if (reviewTask.isSuccessful) {
                reviewInfo = task.result
                return@addOnCompleteListener
            }

            val error = reviewTask.exception?.localizedMessage
            return@addOnCompleteListener
        }
        return Pair(manager, reviewInfo)
    }

    fun reviewUsStart(activity: Activity, manager: ReviewManager, reviewInfo: ReviewInfo?) {
        reviewInfo?.let {
            val flow = manager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        ""
                    }
                    else -> {
                        Log.i("error", it.exception?.localizedMessage.toString())
                        val a = it.exception?.localizedMessage.toString()
                        Log.i("error", a)
                    }
                }
            }
        }
    }
}