package com.finance.trade_learn.view


import TradePage
import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseViewModel
import com.finance.trade_learn.theme.FinanceAppTheme
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.view.history.TradeHistoryScreen
import com.finance.trade_learn.view.loginscreen.forgotpassword.ForgotPasswordScreen
import com.finance.trade_learn.view.loginscreen.login.LoginScreen
import com.finance.trade_learn.view.loginscreen.signup.SignUpScreen
import com.finance.trade_learn.view.profile.ProfileScreen
import com.finance.trade_learn.view.wallet.WalletScreen
import com.finance.trade_learn.viewModel.ForgotPasswordViewModel
import com.finance.trade_learn.viewModel.HomeViewModel
import com.finance.trade_learn.viewModel.LoginViewModel
import com.finance.trade_learn.viewModel.ProfileViewModel
import com.finance.trade_learn.viewModel.SignUpViewModel
import com.finance.trade_learn.viewModel.ViewModelHistoryTrade
import com.finance.trade_learn.viewModel.WalletPageViewModel
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

val LocalWalletPageViewModel =
    compositionLocalOf<WalletPageViewModel> { error("No LocalWalletPageViewModel found") }

val LocalViewModelHistoryTrade =
    compositionLocalOf<ViewModelHistoryTrade> { error("No ViewModelHistoryTrade found") }

val LocalHomeViewModel =
    compositionLocalOf<HomeViewModel> { error("No ViewModelHistoryTrade found") }

val LocalSingUpViewModel = compositionLocalOf<SignUpViewModel> { error("No SignUpViewModel found") }

val LocalProfileViewModel = compositionLocalOf<ProfileViewModel> { error("No ProfileViewModel found") }

val LocalLoginViewModel = compositionLocalOf<LoginViewModel> { error("No LoginViewModel found") }

val LocalForgotPasswordViewModel = compositionLocalOf<ForgotPasswordViewModel> { error("No LoginViewModel found") }

@OptIn(ExperimentalMaterial3Api::class)
private val LocalMainScrollBehavior =
    compositionLocalOf<BottomAppBarScrollBehavior> { error("No BottomAppBarScrollBehavior found") }


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //   private lateinit var firestore: FirebaseFirestore
    private var mInterstitialAd: InterstitialAd? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            FinanceAppTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val baseViewModel = hiltViewModel<BaseViewModel>()

                LaunchedEffect(Unit) {
                    setup()
                    baseViewModel.isLogin(context = context)
                }

                val shouldShowBottomNavigationBar by baseViewModel.shouldShowBottomNavigationBar.observeAsState(
                    true
                )


                val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

                CompositionLocalProvider(
                    LocalBaseViewModel provides baseViewModel,
                    LocalMainScrollBehavior provides scrollBehavior
                ) {
                    Scaffold(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        bottomBar = {
                            if (shouldShowBottomNavigationBar) {
                                BottomNavigationBar(navController = navController)
                            }
                        }
                    ) { padding ->
                        Surface(color = androidx.compose.material.MaterialTheme.colors.secondaryVariant) {
                            MainScreen(navController, Modifier.padding(paddingValues = padding))
                        }
                    }
                }

            }
        }
    }

    @Composable
    private fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier) {
        var marketPageNumber by remember { mutableIntStateOf(2) }

        val context = LocalContext.current

        NavHost(navController = navController, startDestination = Screens.Home.route) {
            composable(Screens.Home.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(true)
                val viewModel = hiltViewModel<HomeViewModel>()

                CompositionLocalProvider(LocalHomeViewModel provides viewModel) {
                    com.finance.trade_learn.view.home.MainView(
                        shouldShowPopularCoins = true,
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        }
                    )
                }
            }
            composable(Screens.Market.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(true)
                val viewModel = hiltViewModel<HomeViewModel>()

                CompositionLocalProvider(LocalHomeViewModel provides viewModel) {
                    com.finance.trade_learn.view.home.MainView(
                        page = marketPageNumber,
                        openTradePage = {
                            navController.navigate(Screens.Trade(it).route)
                        }
                    )
                }


                marketPageNumber++
            }

            composable(
                "trade?coinName={coinName}", arguments = listOf(navArgument("coinName") {
                    type = NavType.StringType
                    defaultValue = "bitcoin"
                })
            ) { backStackEntry ->
                LocalBaseViewModel.current.setBottomNavigationBarStatus(true)

                val coinName = backStackEntry.arguments?.getString("coinName") ?: "TETHER"
                TradePage(itemName = coinName)
            }
            composable(Screens.Wallet.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(true)
                val viewModel = hiltViewModel<WalletPageViewModel>()

                CompositionLocalProvider(LocalWalletPageViewModel provides viewModel) {
                    WalletScreen() {
                        navController.navigate(Screens.HistoryScreen.route)
                    }
                }

            }

            composable(Screens.HistoryScreen.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(false)

                val viewModel = hiltViewModel<ViewModelHistoryTrade>()
                CompositionLocalProvider(LocalViewModelHistoryTrade provides viewModel) {
                    TradeHistoryScreen(modifier = modifier, goBack = {
                        navController.popBackStack()
                    })
                }

            }

            composable(Screens.Profile.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(false)
                val viewModel = hiltViewModel<ProfileViewModel>()


                CompositionLocalProvider(LocalProfileViewModel provides viewModel) {
                    ProfileScreen(
                        onLogOut = {
                            navController.popBackStack()
                        }
                    )
                }

            }

            composable(Screens.Login.route) {
                LocalBaseViewModel.current.setBottomNavigationBarStatus(false)

                val viewModel = hiltViewModel<LoginViewModel>()

                CompositionLocalProvider(LocalLoginViewModel provides viewModel) {
                    LoginScreen(
                        onLogin = {
                            navController.popBackStack()
                        },
                        onSignUp = {
                            navController.navigate(Screens.SingUp.route)
                        },
                        onForgotPassword = {
                            navController.navigate(Screens.ForgotPassword.route)
                        },
                        goBack = {
                            navController.popBackStack()
                        }
                    )
                }

            }

            composable(Screens.ForgotPassword.route) {
                val viewModel = hiltViewModel<ForgotPasswordViewModel>()
                CompositionLocalProvider(LocalForgotPasswordViewModel provides viewModel) {
                    ForgotPasswordScreen(
                        onResetPassword = {
                            navController.navigate(Screens.Login.route)
                        },
                        onBackToLogin = {
                            navController.popBackStack()
                        }

                    )
                }
            }

            composable(Screens.SingUp.route) {

                val viewModel = hiltViewModel<SignUpViewModel>()
                CompositionLocalProvider(LocalSingUpViewModel provides viewModel) {
                    SignUpScreen(
                        onSignUp = {
                            Toast.makeText(context, "on Sign Up completed", Toast.LENGTH_LONG)
                                .show()
                        },
                        onBackToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

            }
        }

    }

    private fun setup() {
        isOneEntering()
        checkIsAdShowed()
        showNotificationPermission()
        //firebaseSave()
        //Smartlook.setupAndStartRecording("49af8b0bc2a7ef077d215bfde0b330a2269559fc")
    }

    private fun showNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostPermission(delay = 3000)
            // NotificationPermissionManager.canAskNotificationPermission(this)
        }

    }

    @SuppressLint("HardwareIds")
    private fun setTestPhone() {
        val androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (androidId != "8d1e30b2ef5afa39") 1 else 2
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostPermission(delay: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(delay)
            withContext(Dispatchers.Main) {
                val requestedPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    requestedPermissions,
                    Constants.POST_NOTIFICATION
                )
            }
        }
    }


    //check is first entering or no ? // if it's first time add 1000 dollars
    private fun isOneEntering() {
        val viewModelUtils = ViewModelUtils()
        val state = viewModelUtils.isOneEntering(this)
        if (state) {
            // create notification
            NotificationWorkManager(3, TimeUnit.DAYS, this)

            val deviceId = UUID.randomUUID()
            SharedPreferencesManager(this).addSharedPreferencesString(
                "deviceId",
                deviceId.toString()
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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


    private fun setInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())

        InterstitialAd.load(
            this,
            "ca-app-pub-2861105825918511/1127322176",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd.show(this@MainActivity)
                    SharedPreferencesManager(this@MainActivity).addSharedPreferencesLong(
                        "interstitialAdLoadedTime",
                        System.currentTimeMillis() + (60 * 60 * 1000)
                    )
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {}

            //   override fun onAdFailedToShowFullScreenContent(adError: AdError?) {}
            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }
    }

    private fun checkIsAdShowed() {
        val isAndroidIdAvailable = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) != "4e79e81765cb66e7"

        if (Constants.SHOULD_SHOW_ADS && isAndroidIdAvailable) {
            lifecycleScope.launch {
                val currentMillis = System.currentTimeMillis()
                val updateTime =
                    SharedPreferencesManager(this@MainActivity).getSharedPreferencesLong(
                        "interstitialAdLoadedTime",
                        currentMillis
                    )
                if (currentMillis < updateTime) return@launch

                MobileAds.initialize(this@MainActivity) {}
                setInterstitialAd()
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val mainViewModel = LocalBaseViewModel.current
    val isLogin by mainViewModel.isLogin.collectAsState()
    val scrollBehavior = LocalMainScrollBehavior.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Renk paleti
    val selectedColor = Color(0xff3B82F6) // Turkuaz (seçilen durumda)
    val unselectedColor = Color(0xFFB0BEC5) // Açık gri (seçilmeyen durumda)

    BottomAppBar(
        scrollBehavior = scrollBehavior,
        containerColor = Color.White,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))

    ) {
        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route

            val otherModifier =
                if (navItem.label == R.string.Trade) Modifier
                    .clip(CircleShape)
                    .background(selectedColor)
                else Modifier

            NavigationBarItem(
                icon = {
                    Image(
                        imageVector = navItem.icon,
                        contentDescription = stringResource(id = navItem.label),
                        modifier = Modifier
                            .size(36.dp)
                            .padding(2.dp)
                            .then(otherModifier),
                        colorFilter = ColorFilter.tint(
                            if (isSelected && navItem.label != R.string.Trade) selectedColor
                            else if (navItem.label == R.string.Trade) Color.White
                            else Color.LightGray
                        )
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = navItem.label),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (navItem.route == Screens.Profile.route && !isLogin){
                        navController.navigate(Screens.Login.route)
                        return@NavigationBarItem
                    }

                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                alwaysShowLabel = true, // Etiketi sadece seçildiğinde göster
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }

    }
}


@Preview
@Composable
private fun BottomBarPreview() {
    FinanceAppTheme {
        val navController = rememberNavController()


        Surface(color = Color.White) {
            BottomNavigationBar(navController = navController)
        }
    }
}

