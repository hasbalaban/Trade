package com.finance.trade_learn.view


import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.BottomAppBarState
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
import androidx.compose.ui.Alignment
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
import com.finance.trade_learn.utils.Constants
import com.finance.trade_learn.utils.RemoteConfigs
import com.finance.trade_learn.utils.Screens
import com.finance.trade_learn.utils.SharedPreferencesManager
import com.finance.trade_learn.view.history.TradeHistoryScreen
import com.finance.trade_learn.view.home.HomeScreen
import com.finance.trade_learn.view.loading.LoadingLottieAnimation
import com.finance.trade_learn.view.loginscreen.codeverification.CodeVerificationScreen
import com.finance.trade_learn.view.loginscreen.forgotpassword.ForgotPasswordScreen
import com.finance.trade_learn.view.loginscreen.login.LoginScreen
import com.finance.trade_learn.view.loginscreen.signup.SignUpScreen
import com.finance.trade_learn.view.market.HorizontalPagerScreen
import com.finance.trade_learn.view.profile.ProfileScreen
import com.finance.trade_learn.view.score_board.ScoreBoard
import com.finance.trade_learn.view.trade.MainBuySellScreen
import com.finance.trade_learn.view.wallet.WalletScreen
import com.finance.trade_learn.viewModel.CodeVerificationViewModel
import com.finance.trade_learn.viewModel.ForgotPasswordViewModel
import com.finance.trade_learn.viewModel.HomeViewModel
import com.finance.trade_learn.viewModel.LoginViewModel
import com.finance.trade_learn.viewModel.ProfileViewModel
import com.finance.trade_learn.viewModel.SignUpViewModel
import com.finance.trade_learn.viewModel.TransactionViewModel
import com.finance.trade_learn.viewModel.TvViewModel
import com.finance.trade_learn.viewModel.ViewModelUtils
import com.finance.trade_learn.viewModel.WalletPageViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


val LocalBaseViewModel = compositionLocalOf<BaseViewModel> { error("No BaseViewModel found") }

val LocalWalletPageViewModel =
    compositionLocalOf<WalletPageViewModel> { error("No LocalWalletPageViewModel found") }

val LocalTvPageViewModel = compositionLocalOf<TvViewModel> { error("No TvViewModel found") }

val LocalViewModelHistoryTrade =
    compositionLocalOf<TransactionViewModel> { error("No ViewModelHistoryTrade found") }

val LocalSingUpViewModel = compositionLocalOf<SignUpViewModel> { error("No SignUpViewModel found") }

val LocalHomeViewModel = compositionLocalOf<HomeViewModel> { error("No HomeViewModel found") }

val LocalProfileViewModel =
    compositionLocalOf<ProfileViewModel> { error("No ProfileViewModel found") }

val LocalLoginViewModel = compositionLocalOf<LoginViewModel> { error("No LoginViewModel found") }

val LocalForgotPasswordViewModel =
    compositionLocalOf<ForgotPasswordViewModel> { error("No LoginViewModel found") }

val LocalCodeVerificationViewModel =
    compositionLocalOf<CodeVerificationViewModel> { error("No CodeVerificationViewModel found") }

@OptIn(ExperimentalMaterial3Api::class)
private val LocalMainScrollBehavior =
    compositionLocalOf<BottomAppBarScrollBehavior> { error("No BottomAppBarScrollBehavior found") }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()

    private var mInterstitialAd: InterstitialAd? = null

    private var runnable = Runnable { }
    val handler = Handler(Looper.getMainLooper())
    private val timeLoop = 30_000L

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val source = intent.getStringExtra("source")

        val message = when(source){
            "shortCut-Market-1" -> "shortCut-Market-1 üzerinden açıldı"
            "shortCut-Market-2" -> "shortCut-Market-2 üzerinden açıldı"
            else -> "Normal açılış"
        }
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        enableEdgeToEdge()

        setContent {
            FinanceAppTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                val bottomAppBarState = BottomAppBarState(0f, 0f, 0f)
                val bottomScrollBehavior =
                    BottomAppBarDefaults.exitAlwaysScrollBehavior(state = bottomAppBarState)


                val isLockedScreen by BaseViewModel.lockMainActivityToAction.observeAsState(true)

                navController.addOnDestinationChangedListener { controller, destination, arguments ->
                    val bottomNavigationIsVisible =
                        destination.route in Constants.BottomNavItems.map { it.route }

                    baseViewModel.setBottomNavigationBarStatus(bottomNavigationIsVisible)
                    BaseViewModel.setLockMainActivityStatus(false)

                    bottomAppBarState.heightOffset = 0f
                }

                LaunchedEffect(Unit) {
                    setup()
                    baseViewModel.checkUserInfo(context = context)
                }


                CompositionLocalProvider(
                    LocalBaseViewModel provides baseViewModel
                ) {
                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(bottomScrollBehavior.nestedScrollConnection),
                        bottomBar = {
                            BottomNavigationBar(
                                navController = navController,
                                scrollBehavior = bottomScrollBehavior
                            )
                        },
                        backgroundColor = MaterialTheme.colors.primary
                    ) { innerPadding ->
                        MainScreen(
                            navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }

                    if (isLockedScreen) {
                        Column(
                            Modifier
                                .clickable(enabled = false, onClick = {})
                                .fillMaxSize()
                                .background(Color(0x80000000)),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            LoadingLottieAnimation(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(alignment = Alignment.CenterHorizontally)
                            )
                        }
                    }

                }

            }
        }
    }

    @Composable
    private fun MainScreen(
        navController: NavHostController,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current

        val baseViewModel = hiltViewModel<BaseViewModel>()


        NavHost(
            modifier = modifier.padding(top = 24.dp),
            navController = navController,
            startDestination = Screens.Home.route
        ) {
            composable(Screens.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                CompositionLocalProvider(LocalHomeViewModel provides viewModel) {
                    HomeScreen(
                        openTradePage = { itemId: String ->
                            navController.navigate(Screens.Trade(itemId).route)
                        },
                        clickedViewAll = {
                            navController.navigate(Screens.Wallet.route)
                        },
                        openMarketPage = {
                            navController.navigate(Screens.Market.route)
                        },
                        navigateToLogin = {
                            navController.navigate(Screens.Login.route)
                        },
                        navigateToSignUp = {
                            navController.navigate(Screens.SingUp.route)
                        }
                    )
                }
            }

            composable(Screens.Market.route) {
                HorizontalPagerScreen(
                    openTradePage = {
                        navController.navigate(Screens.Trade(it).route)
                    },
                    navigateToLogin = {
                        navController.navigate(Screens.Login.route)
                    })
            }

            composable(
                "trade?coinName={coinName}", arguments = listOf(navArgument("coinName") {
                    type = NavType.StringType
                    defaultValue = "bitcoin"
                })
            ) { backStackEntry ->

                val coinName = backStackEntry.arguments?.getString("coinName") ?: "TETHER"
                val viewModel = hiltViewModel<TvViewModel>()
                CompositionLocalProvider(LocalTvPageViewModel provides viewModel) {
                    MainBuySellScreen(
                        itemId = coinName,
                        goBack = {
                            navController.popBackStack()
                        }
                    )
                }


            }
            composable(Screens.Wallet.route) {
                val viewModel = hiltViewModel<WalletPageViewModel>()

                CompositionLocalProvider(LocalWalletPageViewModel provides viewModel) {
                    WalletScreen(
                        goBack = {
                            navController.popBackStack()
                        },
                        navigateToHistoryPage = {
                            navController.navigate(Screens.HistoryScreen.route)
                        },
                        openTradePage = { itemId: String ->
                            navController.navigate(Screens.Trade(itemId).route)
                        },
                    )
                }

            }

            composable(Screens.HistoryScreen.route) {
                val viewModel = hiltViewModel<TransactionViewModel>()
                CompositionLocalProvider(LocalViewModelHistoryTrade provides viewModel) {
                    TradeHistoryScreen(goBack = {
                        navController.popBackStack()
                    })
                }
            }

            composable(Screens.ScoreBoard.route) {
                ScoreBoard()
            }

            composable(Screens.Profile.route) {
                val viewModel = hiltViewModel<ProfileViewModel>()


                CompositionLocalProvider(LocalProfileViewModel provides viewModel) {
                    ProfileScreen(
                        onLogOut = {
                            navController.popBackStack()
                            baseViewModel.checkUserInfo(context)
                        },
                        goTransactionScreen = {
                            navController.navigate(Screens.HistoryScreen.route)
                        },
                        goWalletScreen = {
                            navController.navigate(Screens.Wallet.route)
                        },
                        navigateToHome = {
                            navController.navigate(Screens.Wallet.route) {
                                popUpTo(
                                    navController.graph.startDestinationRoute ?: Screens.Home.route
                                )
                                launchSingleTop = true
                            }
                        }
                    )
                }

            }

            composable(Screens.Login.route) {
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
                            baseViewModel.checkUserInfo(context)
                        }
                    )
                }

            }

            composable(Screens.SingUp.route) {

                val viewModel = hiltViewModel<SignUpViewModel>()
                CompositionLocalProvider(LocalSingUpViewModel provides viewModel) {
                    SignUpScreen(
                        onSignUpCompleted = {
                            Toast.makeText(context, "on Sign Up completed", Toast.LENGTH_LONG)
                                .show()

                            navController.navigate(Screens.Login.route) {
                                popUpTo(
                                    navController.graph.startDestinationRoute ?: Screens.Home.route
                                )
                                launchSingleTop = true
                            }
                        },
                        onBackToLogin = {
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
                            val route = Screens.VerificationCode(it).route
                            navController.navigate(route)
                        },
                        onBackToLogin = {
                            navController.popBackStack()
                        }

                    )
                }
            }


            //"trade?coinName={coinName}"
            //Screens.VerificationCode.route
            composable("verification_code?email={email}", arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
            })) { backStackEntry ->
                val viewModel = hiltViewModel<CodeVerificationViewModel>()
                val userEmail = backStackEntry.arguments?.getString("email") ?: ""
                CompositionLocalProvider(LocalCodeVerificationViewModel provides viewModel) {
                    CodeVerificationScreen(
                        userEmail = userEmail,
                        onVerifyCode = {
                            navController.navigate(Screens.Login.route) {
                                popUpTo(
                                    navController.graph.startDestinationRoute ?: Screens.Home.route
                                )
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

        }

    }

    private fun setup() {
        val viewModelUtils = ViewModelUtils()
        val firstLogin = viewModelUtils.isOneEntering(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostPermission(delay = 10000)
        }

        if (!firstLogin){
            checkIsAdShowed()
        }

        showNotificationPermission()
    }

    private fun showNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPostPermission(delay = 3000)
            // NotificationPermissionManager.canAskNotificationPermission(this)
        }

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

    private fun setInterstitialAd() {

        val testDevices: MutableList<String> = ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDevices)
            .build()

        MobileAds.setRequestConfiguration(requestConfiguration)

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            Constants.ProductionAdKey,
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
        lifecycleScope.launch {
            delay(6_000)
            if (RemoteConfigs.SHOULD_SHOW_ADVERTISEMENT) {
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

    private fun keepDataUpdated() {
        runnable = Runnable {
            runBlocking {
                baseViewModel.getAllCrypto()
            }
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        keepDataUpdated()
    }


    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    scrollBehavior: BottomAppBarScrollBehavior
) {
    val baseViewModel = LocalBaseViewModel.current
    val shouldShowBottomNavigationBar by baseViewModel.shouldShowBottomNavigationBar.observeAsState(
        true
    )

    if (!shouldShowBottomNavigationBar) return


    val isLogin by BaseViewModel.isLogin.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Renk paleti
    val selectedColor = Color(0xff3B82F6) // Turkuaz (seçilen durumda)
    val unselectedColor = Color(0xFFB0BEC5) // Açık gri (seçilmeyen durumda)

    BottomAppBar(
        scrollBehavior = scrollBehavior,
        containerColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))

    ) {
        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route


            NavigationBarItem(
                icon = {
                    Image(
                        imageVector = navItem.icon,
                        contentDescription = stringResource(id = navItem.label),
                        modifier = Modifier
                            .size(if (isSelected && navItem.label == R.string.score_board) 32.dp else 20.dp),
                        colorFilter = ColorFilter.tint(
                            if (isSelected) selectedColor
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
                    if (navItem.route == Screens.Profile.route && !isLogin) {
                        navController.navigate(Screens.Login.route)
                        return@NavigationBarItem
                    }

                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
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


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun BottomBarPreview() {
    FinanceAppTheme {
        val navController = rememberNavController()
        val bottomAppBarState = BottomAppBarState(0f, 0f, 0f)
        val scrollBehavior =
            BottomAppBarDefaults.exitAlwaysScrollBehavior(state = bottomAppBarState)



        Surface(color = Color.White) {
            BottomNavigationBar(navController = navController, scrollBehavior = scrollBehavior)
        }
    }
}

