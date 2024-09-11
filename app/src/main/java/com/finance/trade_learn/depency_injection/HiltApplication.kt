package com.finance.trade_learn.depency_injection

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.finance.trade_learn.R
import com.finance.trade_learn.utils.RemoteConfigs
import com.finance.trade_learn.utils.RemoteConfigsConst
import com.google.firebase.Firebase
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
                    Toast.makeText(this, "Fetch and activate succeeded", Toast.LENGTH_SHORT,).show()

                    RemoteConfigs.SHOULD_BE_LOCAL_REQUEST = remoteConfig.getBoolean(RemoteConfigsConst.SHOULD_BE_LOCAL_REQUEST)
                    RemoteConfigs.SHOULD_SHOW_ADVERTISEMENT = remoteConfig.getBoolean(RemoteConfigsConst.SHOULD_SHOW_ADVERTISEMENT)

                    RemoteConfigs.setRemoteConfigStatus(true)
                } else {
                    Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT,).show()

                    retryFetching.invoke()
                }
            }
    }

}