package com.finance.trade_learn.depency_injection

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.finance.trade_learn.R
import com.finance.trade_learn.utils.RemoteConfigs
import com.finance.trade_learn.utils.RemoteConfigsConst
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltAndroidApp
class HiltApplication : Application() {

    private lateinit var remoteConfig: FirebaseRemoteConfig


    override fun onCreate() {
        super.onCreate()

        setup()
    }

    private fun setup(){
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)

        fetchAndActivateFirebase()

        return
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            handleUncaughtException(thread, exception)
        }
    }



    // Hata yakalayıcı fonksiyon
    private fun handleUncaughtException(thread: Thread, exception: Throwable) {
        // Bellek bilgilerini al
        val memoryInfo = getMemoryInfo(applicationContext)

        FirebaseCrashlytics.getInstance().log("Uncaught Exception on thread: ${thread.name}")
        FirebaseCrashlytics.getInstance().log("Memory Info: $memoryInfo")

        FirebaseCrashlytics.getInstance().recordException(exception)

        // Uygulamayı sonlandır
        android.os.Process.killProcess(android.os.Process.myPid())

    }

    private fun getMemoryInfo(context: Context): String {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val runtime = Runtime.getRuntime()

        val totalMemory = runtime.totalMemory() / (1024 * 1024) // MB cinsinden
        val freeMemory = runtime.freeMemory() / (1024 * 1024) // MB cinsinden
        val maxMemory = runtime.maxMemory() / (1024 * 1024) // MB cinsinden

        return """
        Total Memory: ${memoryInfo.totalMem / (1024 * 1024)} MB
        Available Memory: ${memoryInfo.availMem / (1024 * 1024)} MB
        Low Memory: ${memoryInfo.lowMemory}
        Runtime Total Memory: $totalMemory MB
        Runtime Free Memory: $freeMemory MB
        Runtime Max Memory: $maxMemory MB
    """.trimIndent()
    }

    private fun fetchAndActivateFirebase(){

        val retryFetching = {
            CoroutineScope(Dispatchers.IO).launch {
                delay(2_0000)
                fetchAndActivateFirebase()
            }
        }


        remoteConfig.fetchAndActivate()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("TAG", "Config params updated: $updated")
                    //Toast.makeText(this, "Fetch and activate succeeded", Toast.LENGTH_SHORT,).show()

                    RemoteConfigs.SHOULD_BE_LOCAL_REQUEST = remoteConfig.getBoolean(RemoteConfigsConst.SHOULD_BE_LOCAL_REQUEST)
                    //RemoteConfigs.SHOULD_SHOW_ADVERTISEMENT = remoteConfig.getBoolean(RemoteConfigsConst.SHOULD_SHOW_ADVERTISEMENT)

                    RemoteConfigs.setRemoteConfigStatus(true)
                } else {
                    //Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT,).show()

                    retryFetching.invoke()
                }
            }
    }

}