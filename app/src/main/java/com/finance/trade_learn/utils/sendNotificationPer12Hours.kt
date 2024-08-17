package com.finance.trade_learn.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.finance.trade_learn.R
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.view.MainActivity
import com.finance.trade_learn.viewModel.TradeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SendNotificationPer12Hours @Inject constructor(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        getData()
        return Result.success()
    }

    @Inject
    lateinit var coinDetailRepositoryImp : CoinDetailRepositoryImp
    val viewModel : TradeViewModel by lazy {
        TradeViewModel(coinDetailRepositoryImp)
    }

    fun createNotification(coinName: String, price: String) {
        val channelId = "1"
        val channelName = "Coin Price Notifications"

        Log.i("version", Build.VERSION.SDK_INT.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("version is equals or bigger", Build.VERSION.SDK_INT.toString())

            val priority = NotificationManager.IMPORTANCE_HIGH
            val notifyManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, priority)

            notifyManager.createNotificationChannel(channel)
            val notificationSettings = Notification.Builder(context, channelId)

            val intent = Intent(context,MainActivity::class.java)
            val pendingIntent= PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_MUTABLE)

            notificationSettings.setSubText("Learn Trade")
                .setContentTitle("Coin Name: $coinName")
                .setContentText("Price: $price")
                .setColor(555555)
                .setSmallIcon(R.drawable.coin)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notification = NotificationManagerCompat.from(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notification.notify(1, notificationSettings.build())
            }
            return
        }
        Log.i("version is lower", Build.VERSION.SDK_INT.toString())
    }

    private fun getData() {
        val coinName = SharedPreferencesManager(context).getSharedPreferencesString("coinName")

        CoroutineScope(Dispatchers.IO).launch {
            cryptoService().selectedCoinToTrade(coinName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    DisposableSingleObserver<List<CoinDetail>>() {

                    override fun onSuccess(t: List<CoinDetail>) {
                        createNotification(t[0].symbol, t[0].current_price.toString())
                    }

                    override fun onError(e: Throwable) {}
                })
        }
    }
}


// this will change because we use this fun for tests...
fun NotificationWorkManager(repeatTime : Long, timeUnit : TimeUnit, context: Context) {
    val constraint = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    val myWorkRequest: WorkRequest =
        PeriodicWorkRequestBuilder<SendNotificationPer12Hours>(repeatTime, timeUnit)
            .setConstraints(constraint)
            .build()
    WorkManager.getInstance(context).enqueue(myWorkRequest)
}