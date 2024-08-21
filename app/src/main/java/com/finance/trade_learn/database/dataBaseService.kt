package com.finance.trade_learn.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.finance.trade_learn.database.dataBaseEntities.MyCoins
import com.finance.trade_learn.database.dataBaseEntities.UserTransactions

@Database(entities = arrayOf(MyCoins::class, UserTransactions::class), exportSchema = false, version = 1)
abstract class dataBaseService : RoomDatabase() {

    abstract fun databaseDao(): DatabaseDao

    companion object {

        @Volatile
        var instance: dataBaseService? = null
        val lock = Any()

        operator fun invoke(context: Context) =
            instance ?: synchronized(lock) {

                instance ?: makeDataBase(context).also {
                    instance = it
                }

            }


        fun makeDataBase(context: Context): dataBaseService {
            /*
            val migrate = object : Migration(1, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "CREATE TABLE IF NOT EXISTS SaveCoin(" +
                                "tradeId INT AUTO_INCREMENT PRIMARY KEY," +
                                "coinName  VARCHAR(60)," +
                                "coinAmount  VARCHAR(50)," +
                                "coinPrice  VARCHAR(50)," +
                                "total  VARCHAR(70)," +
                                "date  VARCHAR(70)," +
                                "tradeOperation  VARCHAR(10)) "
                    )
                }

            }
            .addMigrations(migrate).
             */
            return Room.databaseBuilder(
                context, dataBaseService::class.java, "CryptoDataBase"
            ).build()
        }
    }
}