package com.finance.trade_learn.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets

object DialogManager {

    private fun Window.getWidthHeightPair(): Pair<Int, Int> {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            Pair(windowMetrics.bounds.width() - insets.left - insets.right,
                windowMetrics.bounds.height() - insets.top - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    fun Window.configureWindow(padding: Float) {
        val widthHeightPair = getWidthHeightPair()
        val paddingWidths = DisplayUtil.dpToPx(context, padding)
        setLayout(widthHeightPair.first - paddingWidths, ViewGroup.LayoutParams.WRAP_CONTENT)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}