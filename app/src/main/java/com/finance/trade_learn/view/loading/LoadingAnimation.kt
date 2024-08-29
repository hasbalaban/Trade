package com.finance.trade_learn.view.loading

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.finance.trade_learn.R

@Composable
fun LoadingLottieAnimation(modifier: Modifier){

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.animation))
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}