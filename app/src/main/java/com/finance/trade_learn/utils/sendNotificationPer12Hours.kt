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
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel.Companion.setLockMainActivityStatus
import com.finance.trade_learn.repository.CoinDetailRepositoryImp
import com.finance.trade_learn.service.ctryptoApi.cryptoService
import com.finance.trade_learn.view.MainActivity
import com.finance.trade_learn.viewModel.TradeViewModel
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
    }

    private fun getData() {
        val coinName = SharedPreferencesManager(context).getSharedPreferencesString("coinName")

        CoroutineScope(Dispatchers.IO).launch {


            val response = cryptoService().getCoinList()
            setLockMainActivityStatus(false)

            if(response.isSuccessful){
                response.body()?.data?.let {

                    val item = it.firstOrNull { it.id.equals(other = coinName, ignoreCase = true) } ?: return@launch



                    createNotification(item.symbol, item.current_price.toString())
                }
            }
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