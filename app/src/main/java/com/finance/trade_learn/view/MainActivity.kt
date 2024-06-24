package com.finance.trade_learn.view


import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.viewModel.SearchCoinViewModel
import com.finance.trade_learn.viewModel.ViewModelCurrentTrade
import com.finance.trade_learn.viewModel.ViewModelHistoryTrade
import com.finance.trade_learn.viewModel.ViewModelMyWallet
import com.finance.trade_learn.viewModel.ViewModelUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val LocalBaseViewModel = compositionLocalOf<BaseViewModel> { error("No BaseViewModel found") }


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


 //   private lateinit var firestore: FirebaseFirestore
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            FinanceAppTheme{
                val navController = rememberNavController()

                LaunchedEffect(Unit){
                    setup()
                }

                Surface(color = Color.White) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ){padding ->
                        MainScreen(navController)
                    }
                }
        }
    }

    @Composable
    private fun MainScreen(navController: NavHostController) {
        val baseViewModel = hiltViewModel<BaseViewModel>()

        CompositionLocalProvider(LocalBaseViewModel provides baseViewModel) {
            NavHost(navController = navController, startDestination = "home" ){
                composable(Screens.Home.route) {
                    com.finance.trade_learn.view.home.MainView(
                        shouldShowPopularCoins = true,
                        openSearch = {
                            navController.navigate(Screens.SearchScreen.route)
                        },
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        }
                    )
                }
                composable(Screens.Market.route) {
                    com.finance.trade_learn.view.home.MainView(
                        page = 2,
                        openSearch = {
                            navController.navigate(Screens.SearchScreen.route)
                        },
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        }
                    )
                }

                composable("trade?coinName={coinName}", arguments = listOf(navArgument("coinName") {
                    type = NavType.StringType
                    defaultValue = "bitcoin"
                })
                ){backStackEntry->
                    val coinName = backStackEntry.arguments?.getString("coinName") ?: "TETHER"
                    val viewModel = hiltViewModel<ViewModelCurrentTrade>()

                    TradeScreen(
                        openHistoryScreen = {
                            navController.navigate(Screens.HistoryScreen.route)
                        },
                        viewModel = viewModel,
                        coinName = coinName,

                        )
                }
                composable(Screens.Wallet.route) {
                    val viewModel = hiltViewModel<ViewModelMyWallet>()

                    WalletScreen(
                        openSearch = {
                            navController.navigate(Screens.SearchScreen.route)
                        },
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        },
                        viewModel = viewModel
                    )
                }

                composable(Screens.HistoryScreen.route) {
                    val viewModel = hiltViewModel<ViewModelHistoryTrade>()
                    HistoryScreen(viewModel)
                }

                composable(Screens.SearchScreen.route) {
                    val viewModel = hiltViewModel<SearchCoinViewModel>()

                    SearchScreen(
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun setup (){
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
        val isAndroidIdAvailable = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) != "4e79e81765cb66e7"

        if (Constants.SHOULD_SHOW_ADS && isAndroidIdAvailable){
            lifecycleScope.launch {
                val currentMillis = System.currentTimeMillis()
                val updateTime = SharedPreferencesManager(this@MainActivity).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
                if (currentMillis < updateTime) return@launch

                MobileAds.initialize(this@MainActivity) {}
                setInterstitialAd()
            }
        }

    }



    @Composable
    fun BottomNavigationBar(navController: NavHostController) {

        BottomNavigation() {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Constants.BottomNavItems.forEach { navItem ->

                BottomNavigationItem(

                    selected = currentRoute == navItem.route,
                    onClick = {
                        navController.navigate(navItem.route){
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Constants.BottomNavItems.first().route)
                        }
                    },

                    icon = {
                        Image(painter = painterResource(id = navItem.icon), contentDescription = navItem.label)
                    },
                    label = {
                        Text(text = navItem.label)
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }

    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion") || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith(
            "generic"
        )) || "google_sdk" == Build.PRODUCT
    }

}