package com.finance.trade_learn.view

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentCurrentTradeBinding
import com.finance.trade_learn.enums.TradeType
import com.finance.trade_learn.models.CustomAlertFields
import com.finance.trade_learn.models.SelectedPercent
import com.finance.trade_learn.models.coin_gecko.CoinDetail
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.viewModel.ViewModelCurrentTrade
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Runnable
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class CurrentTrade @Inject constructor() : BaseFragmentViewModel<FragmentCurrentTradeBinding, ViewModelCurrentTrade>(FragmentCurrentTradeBinding::inflate), TextWatcher, ReviewUsI {

    private lateinit var toast: Toast
    private var currentPrice = 0.0
    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var tradeState = TradeType.Buy
    private var avaibleAmount : BigDecimal = BigDecimal.valueOf(0.0)

    override val viewModel: ViewModelCurrentTrade by viewModels()

    private var selectedItemName = "bitcoin"
    private var timeLoop = 10000L
    private var job : Job? = null
    private var adInterstitial: InterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        super.onViewCreated(view, savedInstanceState)
    }

    //call this function when viewCrated to initialize addTextChangedListener etc. - get dataFrom Database and Api // Call fun setInitialize()  // call fun startAnimation()- set Click Listener
    private fun setup() {
        toast = Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)

        setInitialize()
        setDataBindingSettings()
        setObservers()
        getDetailsOfCoinFromDatabase()
        startAnimation()
        setInterstitialAd()
        setClickListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners(){
        binding.Buy.setOnClickListener {
            getDetailsOfCoinFromDatabase()
            tradeState = TradeType.Buy
            buyClicked()
        }
        binding.Sell.setOnClickListener {
            getDetailsOfCoinFromDatabase(selectedItemName)
            tradeState = TradeType.Sell
            sellClicked()
        }
        // add one more coin
        binding.minus.setOnClickListener {
            // if amount not equals zero (0)
            if (binding.coinAmount.text.toString() != "" && binding.coinAmount.text.toString().toDouble() != 0.0) {
                val currentAmount = binding.coinAmount.text.toString().toDouble()
                if (currentPrice < 50.0)
                {
                    changeAmounts(currentAmount, 1.000,CoinProgress.MINUS)
                    return@setOnClickListener
                }
                changeAmounts(currentAmount,  0.001,CoinProgress.MINUS)
                return@setOnClickListener
            }
            toastMessages(R.string.trueValue)
        }
        //when we click raise button
        binding.raise.setOnClickListener {
            if (binding.coinAmount.text.toString().isNotEmpty()) {
                val currentAmount = binding.coinAmount.text.toString().toDouble()
                if (currentPrice < 50.0){
                    changeAmounts(currentAmount, 1.000,CoinProgress.SUM)
                    return@setOnClickListener
                }
                changeAmounts(currentAmount,  0.001,CoinProgress.SUM)
                return@setOnClickListener
            }
            val currentAmount = 0.000
            changeAmounts(currentAmount,  1.000,CoinProgress.SUM)
        }

        //when we click do trade button
        binding.doTrade.setOnClickListener {
            val amount = binding.coinAmount.text.toString()
            if (amount != "" || amount != "0.0") {
                //check views and other is empty or not etc.
                val logicalCompare = compare()
                if (logicalCompare) {
                    operationTrade()
                    return@setOnClickListener
                }
                toastMessages(R.string.proggresState)
                return@setOnClickListener
            }
            toastMessages(R.string.enterAmountDialog)
        }
        // navigate last  trade fragment
        binding.historyOfTrade.setOnClickListener {
            val action =CurrentTradeDirections.actionTradePageToHistoryOfTrade2()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.apply {
            percent25Layout.setOnClickListener {
                clickedPercentLayout(SelectedPercent.Percent25,viewModel.tradeType.value)
            }
            percent50Layout.setOnClickListener {
                clickedPercentLayout(SelectedPercent.Percent50, viewModel.tradeType.value)
            }
            percent75Layout.setOnClickListener {
                clickedPercentLayout(SelectedPercent.Percent75, viewModel.tradeType.value)
            }
            percent100Layout.setOnClickListener {
                clickedPercentLayout(SelectedPercent.Percent100, viewModel.tradeType.value)
            }
        }

        binding.minus.setOnTouchListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                if (viewModel.canChangeAmount.value == true) {
                    withContext(Dispatchers.Main){
                        // if amount not equals zero (0)
                        if (binding.coinAmount.text.toString() != "" && binding.coinAmount.text.toString().toDouble() != 0.0) {
                            val currentAmount = binding.coinAmount.text.toString().toDouble()
                            if (currentPrice < 50.0)
                            {
                                changeAmounts(currentAmount, 1.000,CoinProgress.MINUS)
                                return@withContext
                            }
                            changeAmounts(currentAmount,  0.001,CoinProgress.MINUS)
                            return@withContext
                        }
                        toastMessages(R.string.trueValue)
                        viewModel.canChangeAmount.value = false
                        delay(200)
                        viewModel.canChangeAmount.value = true
                    }
                }
            }
            return@setOnTouchListener true
        }

        binding.raise.setOnTouchListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                if (viewModel.canChangeAmount.value == true) {
                    withContext(Dispatchers.Main){
                        if (binding.coinAmount.text.toString().isNotEmpty()) {
                            val currentAmount = binding.coinAmount.text.toString().toDouble()
                            if (currentPrice < 50.0){
                                changeAmounts(currentAmount, 1.000,CoinProgress.SUM)
                                return@withContext
                            }
                            changeAmounts(currentAmount,  0.001,CoinProgress.SUM)
                            return@withContext
                        }
                        val currentAmount = 0.000
                        changeAmounts(currentAmount,  1.000,CoinProgress.SUM)
                        viewModel.canChangeAmount.value = false
                        delay(200)
                        viewModel.canChangeAmount.value = true
                    }
                }
            }
            return@setOnTouchListener true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataBindingSettings(){
        binding.apply {
            coinName.text = "${this@CurrentTrade::selectedItemName.get()} / USD"
            coinAmount.addTextChangedListener(this@CurrentTrade)
            coinPrice.addTextChangedListener(this@CurrentTrade)
        }
    }

    //first start this to get name of we had clicked
    private fun setInitialize() {
        selectedItemName = SharedPreferencesManager(requireContext()).getSharedPreferencesString("coinName")
        //    seekBarsProgress()
    }

    //animation to start
    private fun startAnimation() {

        setAnimation(R.anim.animation_for_buy_button,binding.Buy,400L)
        setAnimation(R.anim.animation_for_sell_button,binding.Sell,400L)
        setAnimation(R.anim.anime_to_right,binding.coinPrice, 400L)
        setAnimation(R.anim.animation_for_buy_button,binding.Total, 100L)
        setAnimation(R.anim.animation_for_buy_button,binding.doTrade,500L)
        setAnimation(R.anim.animation_for_buy_button,binding.historyOfTrade,500L)
        setAnimation(R.anim.animation_for_buy_button,binding.relayoutAmount,300L)
        setAnimation(R.anim.animation_for_buy_button,binding.relayoutAvaible, 500L)
    }

    // this function manager time to get data in per 5 seek.
    //we override here runable as Lambda instead Object
    private fun getCoinDetail() {
        runnable = Runnable { //call this function for update
            viewModel.getSelectedCoinDetails(selectedItemName)
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }
    private fun getDetailsOfCoinFromDatabase(coinName: String = "TETHER") {
        viewModel.getDetailsOfCoinFromDatabase(coinName)
    }

    private fun setObservers (){
        viewModel.coinAmountLiveData.observe(viewLifecycleOwner) {
            it?.let {
                avaibleAmount = it
                binding.avaibleAmount.text = it.toString()
                binding.symbol.text = if (tradeState == TradeType.Buy) "USD" else selectedItemName
            }?: run {
                binding.avaibleAmount.text = ""
                binding.symbol.text = ""
            }

        }

        viewModel.selectedCoinToTradeDetails.observe(viewLifecycleOwner) { coin ->
            coin?.let {
                timeLoop = 12000L
                if (it.isNotEmpty()){
                    currentPrice = coin.firstOrNull()?.current_price ?: 0.0
                    putDataInItemSettings(coin[0])
                }

            }
        }


        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                getDetailsOfCoinFromDatabase(coinName = if (viewModel.tradeType.value == TradeType.Buy) "tether" else selectedItemName)
                toastMessages(R.string.succes)
                reviewUs()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                    //requestPostPermission()
                }
                adInterstitial?.let { showInterstitialAd() } ?: run { setInterstitialAd() }
                return@observe
            }
            toastMessages(R.string.fail)
            adInterstitial?.let { showInterstitialAd() } ?: run { setInterstitialAd() }
        }

    }

    // this fun for binding of fata - change percente/price etc.
    @SuppressLint("SetTextI18n")
    private fun putDataInItemSettings(coin: CoinDetail) {

        try {
            val coinPrice = if (coin.current_price.toString().length>10
                && coin.current_price.toString().subSequence(0,10).last().toString() != "."
            ) coin.current_price.toString().substring(0,10)
            else coin.current_price

            binding.coinPrice.setText(coinPrice.toString())
            binding.coinLogo.setImageSvg(coin.image)

            var coinPercentChange: String = coin.price_change_percentage_24h.toString()
            coinPercentChange = ((coinPercentChange.toDouble()).toString() + "0000").subSequence(0, 5).toString()

            if (coin.price_change_percentage_24h.toString().subSequence(0, 1) == "-") {
                binding.coinChangePercent.text = "$coinPercentChange%"
                binding.coinChangePercent.setTextColor(Color.parseColor("#F6465D"))
                return
            }
            if (coinPercentChange.isEmpty()) {
                binding.coinChangePercent.setText(R.string.fail)
                binding.coinChangePercent.setTextColor(Color.parseColor("#2ebd85"))
                return
            }
            binding.coinChangePercent.text = "+ $coinPercentChange%"
            binding.coinChangePercent.setTextColor(Color.parseColor("#2ebd85"))
        } catch (_: Exception) { }
    }

    //create tost message function
    fun toastMessages(messages: Int = 1) {
        if (messages != 1) {
            toast.cancel()
            toast = Toast.makeText(requireContext(), messages, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun percentCoinController(): Pair<Double,Double> {
        val availableAmount = avaibleAmount.toDouble()
        val buyAbilityAmount = (availableAmount/ currentPrice)
        return Pair(availableAmount,buyAbilityAmount)
    }


    // this fun for check logical states. is emty or avaible is more than i want buy etc.
    private fun compare(): Boolean {
        var operationState = false
        val avaibleAmount = avaibleAmount.toString()
        val total = binding.Total.text.toString()
        val amount = binding.coinAmount.text.toString()
        if (avaibleAmount.isNotEmpty() && total .isNotEmpty() && amount .isNotEmpty() ) {

            operationState = when (tradeState) {
                TradeType.Buy -> {
                    ((avaibleAmount.toDouble() >= total.toDouble()) && (avaibleAmount.toDouble() > 0.0) && (total.toDouble() > 0.0) && (amount.toDouble() > 0.0))
                }
                TradeType.Sell -> {
                    ((avaibleAmount.toDouble() >= amount.toDouble()) && (avaibleAmount.toDouble() > 0.0) && (total.toDouble() > 0.0) && (amount.toDouble() > 0.0))
                }
            }
        }
        return operationState
    }

    // this fun for trade operation - buy , sell operation etc.
    private fun operationTrade() {
        when (tradeState) {
            TradeType.Buy -> {
                val amount = binding.coinAmount.text.toString()
                val price = binding.coinPrice.text.toString()
                if (amount != "" && price != "") {

                    val coinAmount = amount.toDouble()
                    val coinPrice = currentPrice
                    val total = coinAmount * coinPrice

                    viewModel.buyCoin(selectedItemName.lowercase(Locale.getDefault()), coinAmount, total, currentPrice)
                }
            }

            TradeType.Sell -> {
                val amount = binding.coinAmount.text.toString()
                val price = binding.coinPrice.text.toString()
                if (amount != "" && price != "") {

                    val coinAmount = amount.toDouble()
                    val coinPrice = currentPrice
                    val total = coinAmount * coinPrice
                    viewModel.sellCoin(selectedItemName.lowercase(Locale.getDefault()), coinAmount, total, currentPrice)
                }
            }
        }
        viewModel.isSuccess
    }

    // this fun for when we click buy button set backGroung color, text color etc.
    private fun buyClicked() {
        tradeState = TradeType.Buy
        viewModel.changeTradeType(TradeType.Buy)
        getDetailsOfCoinFromDatabase("tether")
        binding.apply {
            Buy.setTextColor(Color.parseColor("#ffffff"))
            Buy.setBackgroundColor(Color.parseColor("#2ebd85"))
            Sell.setTextColor(Color.parseColor("#8e919c"))
            Sell.setBackgroundColor(Color.parseColor("#f5f5f5"))
            doTrade.setBackgroundColor(Color.parseColor("#2ebd85"))
            doTrade.setText(R.string.textBuy)
        }
    }

    // this fun for when we click sell button set backGroung color, text color etc.
    private fun sellClicked() {
        tradeState = TradeType.Sell
        getDetailsOfCoinFromDatabase(selectedItemName)
        viewModel.changeTradeType(TradeType.Sell)

        binding.apply {
            Buy.setTextColor(Color.parseColor("#8e919c"))
            Buy.setBackgroundColor(Color.parseColor("#f5f5f5"))
            Sell.setTextColor(Color.parseColor("#ffffff"))
            Sell.setBackgroundColor(Color.parseColor("#F6465D"))
            doTrade.setBackgroundColor(Color.parseColor("#F6465D"))
            doTrade.setText(R.string.textSell)
        }
    }

    //when program has been suspend stop the api servis operations
    override fun onPause() {
        handler.removeCallbacks(runnable)
        job?.cancel()
        super.onPause()
    }

    // when program on resume start update service and do visible true
    override fun onResume() {
        getCoinDetail()
        super.onResume()
    }

    // this override fun for new total when we changing amount  or when updated price from api
    override fun afterTextChanged(s: Editable?) {
        if (binding.coinAmount.text.toString() != "" && binding.coinPrice.text.toString() != "") {
            val amount = binding.coinAmount.text.toString().toBigDecimal()
            val price = binding.coinPrice.text.toString().toBigDecimal()

            if (amount.toDouble() > 0.0 && price.toDouble() > 0.0) {
                val total = (amount * price).toString()
                val coinPrice = if (total.length>10 && total.subSequence(0,10).last().toString() != ".") total.substring(0,10) else  total
                binding.Total.setText(coinPrice)
            }
            return
        }
        binding.Total.setText("")
    }

    private fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) {
        val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
        val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
        binding.coinAmount.setText(amount)
    }

    enum class CoinProgress (){ SUM,MINUS }

    private fun setAnimation(animation: Int, view: View, animationDuration: Long = 0L ) {
        val animationDetails = AnimationUtils.loadAnimation(requireContext(), animation)
        animationDetails.duration = animationDuration
        view.animation = animationDetails
    }

    private fun clickedPercentLayout(percent: SelectedPercent, tradeType: TradeType?) {
        val percentOptions= arrayOf(binding.percent50Image,binding.percent50Image,binding.percent75Image,binding.percent100Image)
        viewModel.changeSelectedPercent(percent)
        when(tradeType) {
            TradeType.Buy ->{
                buySelectedPercent(percent,percentOptions)
            }
            TradeType.Sell -> {
                sellSelectedPercent(percent,percentOptions)
            }
            else -> {}
        }
    }

    private fun buySelectedPercent(percent: SelectedPercent, percentOptions: Array<ImageView>) {
        when(percent){
            SelectedPercent.Percent25 -> {
                binding.apply {
                    binding.Total.setText((percentCoinController().first *0.25).toString())
                    percentOptions.forEach {
                        percent25Image.setImageResource(R.drawable.buy)

                        arrayOf(percent50Image, percent75Image, percent100Image).forEach {
                            it.setImageResource(R.drawable.percent_not_selected)
                        }
                    } }

                if (percentCoinController().first <= 0.0) return

                val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first* currentPrice * 0.25).toString()
                binding.Total.setText(totalText )
                binding.coinAmount.setText((percentCoinController().first * 0.25 / currentPrice ).toString() )
            }
            SelectedPercent.Percent50 -> {
                binding.apply {
                    binding.Total.setText((percentCoinController().first *0.5).toString())
                    percentOptions.forEach {
                        arrayOf(percent25Image, percent50Image).forEach {
                            it.setImageResource(R.drawable.buy)
                        }
                        arrayOf(percent75Image, percent100Image).forEach {
                            it.setImageResource(R.drawable.percent_not_selected)
                        }
                    }
                    if (percentCoinController().first <= 0.0) return

                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first* currentPrice * 0.5).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first * 0.5 / currentPrice ).toString() )
                }

            }
            SelectedPercent.Percent75 -> {
                binding.apply {
                    binding.Total.setText((percentCoinController().first *0.75).toString())
                    percentOptions.forEach {
                        arrayOf(percent25Image, percent50Image, percent75Image).forEach {
                            it.setImageResource(R.drawable.buy)
                        }
                        percent100Image.setImageResource(R.drawable.percent_not_selected)
                    }
                    if (percentCoinController().first <= 0.0) return

                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first* currentPrice*0.75).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first * 0.75 / currentPrice ).toString() )
                }

            }
            SelectedPercent.Percent100 -> {
                binding.apply {
                    binding.Total.setText((percentCoinController().first *1).toString())
                    percentOptions.forEach {
                        arrayOf(percent25Image, percent50Image, percent75Image, percent100Image).forEach {
                            it.setImageResource(R.drawable.buy)
                        }
                    }
                    if (percentCoinController().first <= 0.0) return

                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first* currentPrice).toString()
                    binding.Total.setText(totalText )

                    binding.coinAmount.setText((percentCoinController().first  / currentPrice ).toString() )
                }
            }
        }
    }

    private fun sellSelectedPercent(percent: SelectedPercent, percentOptions: Array<ImageView>) {
        when(percent){
            SelectedPercent.Percent25 -> {
                binding.apply {
                    percentOptions.forEach {
                        percent25Image.setImageResource(R.drawable.sell)
                        arrayOf(percent50Image, percent75Image, percent100Image).forEach {
                            it.setImageResource(R.drawable.percent_not_selected)
                        }
                    }
                    if (percentCoinController().first <= 0.0) return
                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first * currentPrice * 0.25 ).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first  * 0.25 ).toString() )
                }
            }
            SelectedPercent.Percent50 -> {
                binding.apply {
                    percent25Image.setImageResource(R.drawable.sell)
                    percent50Image.setImageResource(R.drawable.sell)
                    arrayOf(percent75Image, percent100Image).forEach {
                        it.setImageResource(R.drawable.percent_not_selected)
                    }

                    if (percentCoinController().first <= 0.0) return
                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first * currentPrice * 0.5 ).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first * 0.5 ).toString() )}

            }
            SelectedPercent.Percent75 -> {
                binding.apply {
                    arrayOf(percent50Image, percent75Image, percent100Image).forEach {
                        it.setImageResource(R.drawable.sell)
                    }
                    percent100Image.setImageResource(R.drawable.percent_not_selected)

                    if (percentCoinController().first <= 0.0) return
                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first * currentPrice * 0.75 ).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first * 0.75 ).toString() )}

            }
            SelectedPercent.Percent100 -> {
                binding.apply {
                    arrayOf(percent25Image, percent50Image, percent75Image, percent100Image).forEach {
                        it.setImageResource(R.drawable.sell)
                    }
                    if (percentCoinController().first <= 0.0) return
                    val totalText = if (currentPrice == 0.0)  "0" else (percentCoinController().first * currentPrice * 1 ).toString()
                    binding.Total.setText(totalText )
                    binding.coinAmount.setText((percentCoinController().first * 1 ).toString() )
                }}
        }
    }

    private fun reviewUs(){
        val reviewUs : ReviewUsI = this
        val reviewUsResult =  reviewUs.reviewUsRequestCompleteListener(activity = requireActivity(), context = requireContext())
        reviewUs.reviewUsStart(activity = requireActivity(), manager = reviewUsResult.first, reviewInfo = reviewUsResult.second)
    }

    private fun showInterstitialAd() {
        adInterstitial?.apply {
            show(requireActivity())
            adInterstitial = null
            SharedPreferencesManager(requireContext()).addSharedPreferencesLong("interstitialAdLoadedTime",System.currentTimeMillis()+(60*60*1000))
        }
    }

    private fun setInterstitialAd() {
        if (Constants.SHOULD_SHOW_ADS.not()) return
        val currentMillis = System.currentTimeMillis()
        val updateTime = SharedPreferencesManager(requireContext()).getSharedPreferencesLong("interstitialAdLoadedTime", currentMillis)
        if (currentMillis < updateTime) return

        val adRequest = AdRequest.Builder().build()
        MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())
        InterstitialAd.load(requireContext(), "ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {}
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                adInterstitial = interstitialAd
            }
        })
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPostPermission(){
        if (NotificationPermissionManager.canAskNotificationPermission(requireActivity())){
            val requestedPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            ActivityCompat.requestPermissions(requireActivity(), requestedPermissions, Constants.POST_NOTIFICATION)
            return
        }
        AlertDialogCustomBuilder.showNotificationPermissionPopup(requireContext(), layoutInflater, CustomAlertFields(R.drawable.notification_icon, getString(R.string.notification_title), getString(R.string.notification_subTitle), getString(R.string.notification_allow_button),null), requireActivity()).show()
    }
}