package com.finance.trade_learn.database.dataBaseEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SaveCoin")
data class UserTransactions(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tradeId")
    val id: Int = 0,

    @ColumnInfo(name = "coinName")
    val transactionItemName: String,

    @ColumnInfo(name = "coinAmount")
    val amount: String,

    @ColumnInfo(name = "coinPrice")
    val price: String,

    @ColumnInfo(name = "total")
    val transactionTotalPrice: String,

    // tradeEnum: tradeEnum
    @ColumnInfo(name = "tradeOperation")
    val transactionType: String,

    @ColumnInfo(name = "date")
    val date: String,

)