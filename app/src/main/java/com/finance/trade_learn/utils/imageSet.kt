package com.finance.trade_learn.utils

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

import coil.load
import coil.transform.CircleCropTransformation
import com.finance.trade_learn.R


fun ImageView.setImage( context: Context, URL:String){
        Glide.with(context) .load(URL).into(this)
}



@BindingAdapter("imageUrl")
fun ImageView.setImageSvg(URL:String){
        this.load(URL) {
                crossfade(true)
                crossfade(1000)
                transformations(CircleCropTransformation())
        }

}




