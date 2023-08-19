package com.finance.trade_learn.view


import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import androidx.navigation.navArgument
import androidx.navigation.ui.setupWithNavController
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ActivityMainBinding
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.viewModel.ViewModelMarket
import com.finance.trade_learn.viewModel.ViewModelUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var dataBindingMain: ActivityMainBinding
    private lateinit var viewModelMarket: ViewModelMarket


 //   private lateinit var firestore: FirebaseFirestore
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //dataBindingMain = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(dataBindingMain.root)
        //viewModelMarket = ViewModelProvider(this)[ViewModelMarket::class.java]
        //setup()

        setContent {
            MainScreen()
        }
    }

    @Composable
    private fun MainScreen(){
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "homeScreen" ){
            composable("homeScreen") {
                MainView(
                    openSearch = {
                        navController.navigate("searchScreen")
                    },
                    openTradePage = {
                        navController.navigate("tradeScreen?coinName=$it")
                    }
                )
            }
            composable("searchScreen") {

                SearchView(
                    openTradePage = {
                        navController.navigate("tradeScreen?coinName=$it")
                    }
                )
            }
            composable("tradeScreen?coinName={coinName}", arguments = listOf(navArgument("coinName") {
                type = NavType.StringType
                defaultValue = "bitcoin"
            })
            ) {backStackEntry->
                CurrentTradeView(backStackEntry.arguments?.getString("coinName"))
            }


        }

    }

    private fun setup (){
        bottomNavigationItemClickListener()
        isOneEntering()
        checkIsAdShowed()
        showNotificationPermission()
        //firebaseSave()
        //Smartlook.setupAndStartRecording("49af8b0bc2a7ef077d215bfde0b330a2269559fc")
    }

    private fun showNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            requestPostPermission(delay = 3000)
           // NotificationPermissionManager.canAskNotificationPermission(this)
        }

    }

    @SuppressLint("HardwareIds")
    private fun setTestPhone (){
        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != "8d1e30b2ef5afa39") 1 else 2
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostPermission(delay : Long){
        CoroutineScope(Dispatchers.IO).launch {
            delay(delay)
            withContext(Dispatchers.Main){
                val requestedPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                ActivityCompat.requestPermissions(this@MainActivity, requestedPermissions, Constants.POST_NOTIFICATION)
            }
        }
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
            SharedPreferencesManager(this).addSharedPreferencesString(
                "deviceId",
                deviceId.toString()
            )

            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
                requestPostPermission(delay = 10000)
            }
        }
    }
    fun firebaseSave() {

      //  firestore = Firebase.firestore
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val deviceID = SharedPreferencesManager(this).getSharedPreferencesString("deviceId", "0")
        val openAppDetails = hashMapOf(
            "open" to "1",
            "time" to currentDate,
            "country" to Locale.getDefault().country,
            "deviceID" to deviceID
        )
        if (deviceID != "057eea2e-396c-4117-b5d4-782b247000f9") {// this condotion will be delete
          //  firestore.collection("StartApp").add(openAppDetails).addOnSuccessListener {
         //   }.addOnFailureListener {

          //  }
        }
    }


    private fun setInterstitialAd(){
        val adRequest = AdRequest.Builder().build()
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())

        InterstitialAd.load(this,"ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.show(this@MainActivity)
                SharedPreferencesManager(this@MainActivity).addSharedPreferencesLong("interstitialAdLoadedTime",System.currentTimeMillis()+(60*60*1000))
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {}
         //   override fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
            override fun onAdShowedFullScreenContent() { mInterstitialAd = null }
        }
    }

    private fun checkIsAdShowed(){
        if (Constants.SHOULD_SHOW_ADS.not()) return
        lifecycleScope.launch {
            val currentMillis = System.currentTimeMillis()
            val updateTime = SharedPreferencesManager(this@MainActivity).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
            if (currentMillis < updateTime) return@launch

            MobileAds.initialize(this@MainActivity) {}
            setInterstitialAd()
        }
    }

}