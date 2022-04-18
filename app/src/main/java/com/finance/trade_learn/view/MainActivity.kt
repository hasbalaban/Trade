package com.finance.trade_learn.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ActivityMainBinding
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.utils.testWorkManager
import com.finance.trade_learn.viewModel.ViewModelMarket
import com.finance.trade_learn.viewModel.viewModelUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartlook.sdk.smartlook.Smartlook
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var dataBindingMain: ActivityMainBinding
    private lateinit var viewModelUtils: viewModelUtils
    private lateinit var viewModelMarket: ViewModelMarket


    private lateinit var firestore: FirebaseFirestore
    private var mInterstitialAd: InterstitialAd? = null

    // val disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        providers()
        super.onCreate(savedInstanceState)
        dataBindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != "8d1e30b2ef5afa39") Smartlook.setupAndStartRecording("49af8b0bc2a7ef077d215bfde0b330a2269559fc")


        bottomNavigationItemClickListener()
        isOneEntering()
        //firebaseSave()


        MobileAds.initialize(this) {}
        checkIsAdShowed()
    }


    private fun providers() {
        viewModelMarket = ViewModelProvider(this).get(ViewModelMarket::class.java)
    }


    // to navigate according click in fragment
    private fun bottomNavigationItemClickListener() {

        controller = findNavController(R.id.fragmentContainerView)
        dataBindingMain.options.setupWithNavController(controller)
        dataBindingMain.options.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

    }

    //check is first entering or no ? // if it's first time add 1000 dollars
    private fun isOneEntering() {
        viewModelUtils = viewModelUtils()
        val state = viewModelUtils.isOneEntering(this)
        if (state) {
            // these functions just for test
            testWorkManager()
            Log.i("first", "this is first Entering")

            val deviceId = UUID.randomUUID()
            sharedPreferencesManager(this).addSharedPreferencesString(
                "deviceId",
                deviceId.toString()
            )


        } else
            Log.i("firstNot", "this is not first Entering")

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
                print("fail1")
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                print("success")
                interstitialAd.show(this@MainActivity)
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                print("fail2")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                print("fail3")
            }

            override fun onAdShowedFullScreenContent() {
                print("success2")
                mInterstitialAd = null
            }
        }
    }

    private fun checkIsAdShowed(){
        val sharedManager = sharedPreferencesManager(this@MainActivity)
        //var adCounter = sharedManager.getSharedPreferencesInt("AdCounter",0)
        //val adDay =sharedManager.getSharedPreferencesInt("AdDate",Calendar.DAY_OF_YEAR)

        //if (adCounter>=3 && adDay == Calendar.DAY_OF_YEAR) return
        //if (adCounter == 3)  {
        //    sharedManager.addSharedPreferencesInt("AdCounter",0)
        //    adCounter = sharedManager.getSharedPreferencesInt("AdCounter",0)
        //}

        //sharedManager.addSharedPreferencesInt("AdCounter",adCounter+1)
        //sharedManager.addSharedPreferencesInt("AdDate",Calendar.DAY_OF_YEAR)

        setInterstitialAd()
    }

}