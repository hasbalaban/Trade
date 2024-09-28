package com.finance.trade_learn.database.dataBaseEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.finance.trade_learn.models.create_new_model_for_tem_history.NewModelForItemHistory
import java.math.BigDecimal

@Entity(tableName = "myCoins")
data class MyCoins(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "CoinName")
    var CoinName: String="",

    @ColumnInfo(name = "CoinAmount")
    var CoinAmount: Double
)

data class TableRow(
    val Sembol: String,
    val Alış: String,
    val Satış: String,
    val Yüksek: String,
    val Düşük: String,
    val Fark: String,
    val FarkYüzde: String,
    val Zaman: String
)

fun List<TableRow>.toMap(): List<NewModelForItemHistory> {

    return map {
        NewModelForItemHistory(
            CoinName = it.Sembol,
            CoinAmount =  it.Alış.toDouble(),
            Total = it.Satış.toDouble().toBigDecimal(),
            Image = null,
            currentPrice = it.Alış,
            percentChange = it.FarkYüzde
        )
    }
}