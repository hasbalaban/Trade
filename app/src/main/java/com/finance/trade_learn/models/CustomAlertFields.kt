package com.finance.trade_learn.models

import androidx.annotation.DrawableRes
import java.io.Serializable

data class CustomAlertFields(
    @DrawableRes val imageResId: Int?,
    val title: String?,
    val subtitle: String?,
    val positiveButtonText: String?,
    val negativeButtonText: String?
) : Serializable