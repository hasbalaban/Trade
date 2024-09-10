package com.finance.trade_learn.models

import com.finance.trade_learn.R

enum class FilterType(var text : Int){
    Default(text = R.string.default_text),
    LowestPrice(text = R.string.lowest_price),
    HighestPrice(text = R.string.highest_price),
    LowestPercentage(text = R.string.lowest_percentage),
    HighestPercentage(text = R.string.highest_percentage)
}