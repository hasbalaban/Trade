package com.finance.trade_learn.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

import coil.load
import coil.transform.CircleCropTransformation


fun ImageView.setImage( context: Context, URL:String){
        Glide.with(context) .load(URL).into(this)
}



fun ImageView.setImageSvg(URL:String){
        this.load(URL) {
                crossfade(true)
                crossfade(1000)
                transformations(CircleCropTransformation())
        }

}




