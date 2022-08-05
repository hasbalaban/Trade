package com.finance.trade_learn.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ActivityMainBinding
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.utils.NotificationWorkManager
import com.finance.trade_learn.viewModel.ViewModelMarket
import com.finance.trade_learn.viewModel.ViewModelUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var dataBindingMain: ActivityMainBinding
    private lateinit var viewModelMarket: ViewModelMarket


    private lateinit var firestore: FirebaseFirestore
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModelMarket = ViewModelProvider(this).get(ViewModelMarket::class.java)
        setup()
    }

    private fun setup (){
        bottomNavigationItemClickListener()
        isOneEntering()
        //firebaseSave()
     //   checkIsAdShowed()
     //   Smartlook.setupAndStartRecording("49af8b0bc2a7ef077d215bfde0b330a2269559fc")
    }

    private fun setTestPhone (){
        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != "8d1e30b2ef5afa39") 1 else 2
    }

    // to navigate according click in fragment
    private fun bottomNavigationItemClickListener() {
        controller = findNavController(R.id.fragmentContainerView)
        dataBindingMain.options.setupWithNavController(controller)
        dataBindingMain.options.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

    }

    //check is first entering or no ? // if it's first time add 1000 dollars
    private fun isOneEntering() {
        val viewModelUtils = ViewModelUtils()
        val state = viewModelUtils.isOneEntering(this)
        if (state) {
            // create notification
            NotificationWorkManager(3,TimeUnit.DAYS,this)

            val deviceId = UUID.randomUUID()
            sharedPreferencesManager(this).addSharedPreferencesString(
                "deviceId",
                deviceId.toString()
            )


        }
    }
    fun firebaseSave() {

        firestore = Firebase.firestore
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val deviceID = sharedPreferencesManager(this).getSharedPreferencesString("deviceId", "0")
        val openAppDetails = hashMapOf(
            "open" to "1",
            "time" to currentDate,
            "country" to Locale.getDefault().country,
            "deviceID" to deviceID
        )
        if (deviceID != "057eea2e-396c-4117-b5d4-782b247000f9") {// this condotion will be delete
            firestore.collection("StartApp").add(openAppDetails).addOnSuccessListener {
            }.addOnFailureListener {

            }
        }
    }


    private fun setInterstitialAd(){
        val adRequest = AdRequest.Builder().build()
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .build()
        )

        InterstitialAd.load(this,"ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.show(this@MainActivity)
                sharedPreferencesManager(this@MainActivity).addSharedPreferencesLong("interstitialAdLoadedTime",System.currentTimeMillis()+(60*60*1000))
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {}
            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
            override fun onAdShowedFullScreenContent() { mInterstitialAd = null }
        }
    }

    private fun checkIsAdShowed(){
        lifecycleScope.launchWhenCreated {
            val currentMillis = System.currentTimeMillis()
            val updateTime = sharedPreferencesManager(this@MainActivity).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
            if (currentMillis < updateTime) return@launchWhenCreated

            MobileAds.initialize(this@MainActivity) {}
            setInterstitialAd()
        }
    }

}