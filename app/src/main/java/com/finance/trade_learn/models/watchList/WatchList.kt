package com.finance.trade_learn.models.watchList



data class WatchListRequestItem (
    val userId : Int,
    val itemId :String,
    val isRemoved : Boolean
)

data class WatchListItem (
    val itemId :String,
    val addedTime : Long
)