package com.finance.trade_learn.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.finance.trade_learn.R
import com.finance.trade_learn.clickListener.ListenerInterface
import com.finance.trade_learn.databinding.FragmentCurrentTradeBinding
import com.finance.trade_learn.enums.tradeEnum
import com.finance.trade_learn.models.on_crypto_trade.BaseModelOneCryptoModel
import com.finance.trade_learn.utils.*
import com.finance.trade_learn.viewModel.viewModelCurrentTrade
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*
import java.lang.Runnable


class CurrentTrade : Fragment(), TextWatcher, ReviewUsI,View.OnTouchListener {

    private lateinit var toast: Toast
    private var currentPrice = 0.0
    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var dataBindingCurrentTrade: FragmentCurrentTradeBinding
    private var tradeState = tradeEnum.Buy

    private val viewModel by viewModels<viewModelCurrentTrade>{
        CurrentTradeViewModelFactory(requireContext())
    }
    private var coinName = "BTC"
    private var timeLoop = 2000L
    private var job : Job? = null
    private var adInterstitial: InterstitialAd? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBindingCurrentTrade = DataBindingUtil.inflate(inflater, R.layout.fragment_current_trade, container, false)
        return dataBindingCurrentTrade.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
        super.onViewCreated(view, savedInstanceState)
    }

    //call this function when viewCrated to initialize addTextChangedListener etc.
    // get dataFrom Database and Api // Call fun setInitialize()
    //call fun startAnimation()- set Click Listener
    private fun setup() {
        toast = Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)
        //viewModel = viewModelCurrentTrade(requireContext())

        setInitialize()
        setDataBindingSettings()
        setObservers()
        getDetailsOfCoinFromDatabase()
        startAnimation()
        setAd()
        setInterstitialAd()
        longClickLister()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun longClickLister(){
        dataBindingCurrentTrade.minus.setOnTouchListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                if (viewModel.canChangeAmount.value == true) {
                    withContext(Dispatchers.Main){
                        clickListenerInitialize.clickListener(dataBindingCurrentTrade.minus)
                        viewModel.canChangeAmount.value = false
                        delay(200)
                        viewModel.canChangeAmount.value = true
                    }
                }
            }
            return@setOnTouchListener true
        }

        dataBindingCurrentTrade.raise.setOnTouchListener { _, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                if (viewModel.canChangeAmount.value == true) {
                    withContext(Dispatchers.Main){
                        clickListenerInitialize.clickListener(dataBindingCurrentTrade.raise)
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
        dataBindingCurrentTrade.apply {
            coinName.text = "${this@CurrentTrade::coinName.get()} / USDT"
            clickLisener = clickListenerInitialize
            coinAmount.addTextChangedListener(this@CurrentTrade)
            coinPrice.addTextChangedListener(this@CurrentTrade)
        }
    }

    private fun setAd() {
        if (System.currentTimeMillis()<1653298595591) return

        val currentMillis = System.currentTimeMillis()
        val updateTime = sharedPreferencesManager(requireContext()).getSharedPreferencesLong("currentTrade",currentMillis)
        val delayTime = if (currentMillis >= updateTime) 0L else updateTime-currentMillis
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            withContext(Dispatchers.Main) {
                dataBindingCurrentTrade.adView.apply {
                    loadAd(AdRequest.Builder().build())
                    adListener = Ads.listenerAdRequest(dataBindingCurrentTrade.adView,"currentTrade",requireContext())
                }
            }
        }
    }

    //first start this to get name of we had clicked
    private fun setInitialize() {
        coinName = sharedPreferencesManager(requireContext()).getSharedPreferencesString("coinName")
         seekBarsProgress()
    }

    //animation to start
    private fun startAnimation() {

        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.Buy,400L)
        setAnimation(requireContext(),R.anim.animation_for_sell_button,dataBindingCurrentTrade.Sell,400L)
        setAnimation(requireContext(),R.anim.anime_to_right,dataBindingCurrentTrade.coinPrice, 400L)
        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.Total, 100L)
        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.doTrade,500L)
        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.historyOfTrade,500L)
        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.relayoutAmount,300L)
        setAnimation(requireContext(),R.anim.animation_for_buy_button,dataBindingCurrentTrade.relayoutAvaible, 500L)
    }

    // this function manager time to get data in per 5 seek.
    //we override here runable as Lambda instead Object
    private fun upDatePer5Sec() {
        runnable = Runnable { //call this function for update
            viewModel.getSelectedCoinDetails(coinName)
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }

    // we getting data from database when fragment starting and after any trade
    fun getDetailsOfCoinFromDatabase(coinName: String = "USDT") {
        viewModel.getDetailsOfCoinFromDatabase(coinName)
    }

    private fun setObservers (){
        viewModel.coinAmountLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                val text = (it.toString())
                dataBindingCurrentTrade.avaibleAmount.text = text
                dataBindingCurrentTrade.symbol.text = coinName
            } else
                dataBindingCurrentTrade.avaibleAmount.text = ""
        }

        viewModel.selectedCoinToTradeDetails.observe(viewLifecycleOwner) { coin ->
            coin?.let {
                timeLoop = 7000L
                if (it.isNotEmpty()){
                    currentPrice = coin[0].price.toDouble()
                    putDataInItemSettings(coin[0])
                    // after it get data from api initialize max of seek bar
                    maxOfSeekBar()
                }

            }
        }

    }

    // this fun for binding of fata - change percente/price etc.
    @SuppressLint("SetTextI18n")
    private fun putDataInItemSettings(coin: BaseModelOneCryptoModel) {

        try {
            val coinPrice = if ( coin.price.length>10 && coin.price.subSequence(0,10).last().toString() != ".") coin.price.substring(0,10) else  coin.price

            dataBindingCurrentTrade.coinPrice.setText(coinPrice)
            dataBindingCurrentTrade.coinLogo.setImageSvg(coin.logo_url)

            var coinPercentChange: String = coin.d1.price_change_pct
            coinPercentChange = ((coinPercentChange.toDouble() * 100.0).toString() + "0000").subSequence(0, 5).toString()

            if (coin.d1.price_change_pct.subSequence(0, 1) == "-") {
                dataBindingCurrentTrade.coinChangePercent.setText(coinPercentChange + "%")
                dataBindingCurrentTrade.coinChangePercent.setTextColor(Color.parseColor("#F6465D"))
                return
            }
            if (coinPercentChange.isEmpty()) {
                dataBindingCurrentTrade.coinChangePercent.setText(R.string.fail)
                dataBindingCurrentTrade.coinChangePercent.setTextColor(Color.parseColor("#2ebd85"))
                return
            }
                dataBindingCurrentTrade.coinChangePercent.setText("+ " + coinPercentChange + "%")
                dataBindingCurrentTrade.coinChangePercent.setTextColor(Color.parseColor("#2ebd85"))
        } catch (e: Exception) {
            Log.i("error", e.message.toString())
        }
    }

    //create tost message function
    fun toastMessages(messages: Int = 1) {
        if (messages != 1) {
            toast.cancel()
            toast = Toast.makeText(requireContext(), messages, Toast.LENGTH_SHORT)
            toast.show()
        }
    }


    // listener override here and initialize
    private val clickListenerInitialize = object : ListenerInterface {
        override fun clickListener(view: View) {
            when (view.id) {
                dataBindingCurrentTrade.Buy.id -> {
                    getDetailsOfCoinFromDatabase()
                    tradeState = tradeEnum.Buy
                    buyClicked()
                }
                dataBindingCurrentTrade.Sell.id -> {
                    getDetailsOfCoinFromDatabase(coinName)
                    tradeState = tradeEnum.Sell
                    sellClicked()
                }
                // add one more coin
                dataBindingCurrentTrade.minus.id -> {
                    // if amount not equals zero (0)
                    if (dataBindingCurrentTrade.coinAmount.text.toString() != "" && dataBindingCurrentTrade.coinAmount.text.toString().toDouble() != 0.0) {
                        val currentAmount = dataBindingCurrentTrade.coinAmount.text.toString().toDouble()
                        if (currentPrice < 50.0)
                        {
                            changeAmounts(currentAmount, 1.000,CoinProgress.MINUS)
                            return
                        }
                        changeAmounts(currentAmount,  0.001,CoinProgress.MINUS)
                        return
                        }
                        toastMessages(R.string.trueValue)
                        return
                }
                //when we click raise button
                dataBindingCurrentTrade.raise.id -> {
                    if (dataBindingCurrentTrade.coinAmount.text.toString().isNotEmpty()) {
                        val currentAmount = dataBindingCurrentTrade.coinAmount.text.toString().toDouble()
                        if (currentPrice < 50.0){
                            changeAmounts(currentAmount, 1.000,CoinProgress.SUM)
                        }
                        else {
                            changeAmounts(currentAmount,  0.001,CoinProgress.SUM)
                        }
                        return
                    }
                    val currentAmount = 0.000
                    changeAmounts(currentAmount,  1.000,CoinProgress.SUM)
                }

                //when we click do trade button
                dataBindingCurrentTrade.doTrade.id -> {
                    val amount = dataBindingCurrentTrade.coinAmount.text.toString()
                    if (amount != "" || amount != "0.0") {
                        //check views and other is empty or not etc.
                        val logicalCompare = compare()
                        if (logicalCompare) {
                            operationTrade()
                        } else {
                            toastMessages(R.string.proggresState)
                        }
                    } else {
                        toastMessages(R.string.enterAmountDialog)
                    }
                }
                // navigate last  trade fragment
                dataBindingCurrentTrade.historyOfTrade.id -> {
                    val action =CurrentTradeDirections.actionTradePageToHistoryOfTrade2()
                    Navigation.findNavController(dataBindingCurrentTrade.root).navigate(action)
                }
            }
        }
    }

    // create ax value of seek bar
    fun maxOfSeekBar(): Int {
        var max = 0
        val avaibleAmount = dataBindingCurrentTrade.avaibleAmount.text.toString().toDouble().toInt()
        when (tradeState) {
            tradeEnum.Sell -> {
                max = avaibleAmount
                dataBindingCurrentTrade.percentOfAvaible.max = max
            }
            tradeEnum.Buy -> {
                max = (avaibleAmount / currentPrice).toInt()
                dataBindingCurrentTrade.percentOfAvaible.max = max
            }
        }
        return max
    }

    // function for trade with seek seekBarsProgress
    private fun seekBarsProgress() {
        dataBindingCurrentTrade.percentOfAvaible.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //for seek percent
                val percentAmount = dataBindingCurrentTrade.percentOfAvaible.progress
                dataBindingCurrentTrade.percentOfAvaible.minimumHeight = 0
                when (tradeState) {

                    tradeEnum.Buy -> {
                        dataBindingCurrentTrade.percentOfAvaible.max = maxOfSeekBar()
                        dataBindingCurrentTrade.coinAmount.setText(percentAmount.toString())
                        if (percentAmount.toString() == "0") {
                            dataBindingCurrentTrade.Total.setText(R.string.addZeros)
                        }
                    }
                    tradeEnum.Sell -> {
                        dataBindingCurrentTrade.percentOfAvaible.max = maxOfSeekBar()
                        if (percentAmount.toString() == "0") {
                            dataBindingCurrentTrade.Total.setText(R.string.addZeros)
                        }
                        dataBindingCurrentTrade.coinAmount.setText(percentAmount.toString())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // this fun for check logical states. is emty or avaible is more than i want buy etc.
    fun compare(): Boolean {
        var operationState = false
        val avaibleAmount = dataBindingCurrentTrade.avaibleAmount.text.toString()
        val total = dataBindingCurrentTrade.Total.text.toString()
        val amount = dataBindingCurrentTrade.coinAmount.text.toString()
        if (avaibleAmount != "" && total != "" && amount != "") {

            operationState = when (tradeState) {
                tradeEnum.Buy -> {
                    ((avaibleAmount.toDouble() >= total.toDouble()) && (avaibleAmount.toDouble() > 0.0) && (total.toDouble() > 0.0) && (amount.toDouble() > 0.0))
                }
                tradeEnum.Sell -> {
                    ((avaibleAmount.toDouble() >= amount.toDouble()) && (avaibleAmount.toDouble() > 0.0) && (total.toDouble() > 0.0) && (amount.toDouble() > 0.0))
                }
            }
        }
        return operationState
    }

    // this fun for trade operation - buy , sell operation etc.
    fun operationTrade() {
        when (tradeState) {
            tradeEnum.Buy -> {
                val amount = dataBindingCurrentTrade.coinAmount.text.toString()
                val price = dataBindingCurrentTrade.coinPrice.text.toString()
                if (amount != "" && price != "") {

                    val coinAmount = amount.toDouble()
                    val coinPrice = currentPrice
                    val total = coinAmount * coinPrice

                    viewModel.buyCoin(coinName, coinAmount, total, currentPrice)
                    viewModel.state.observe(viewLifecycleOwner) {
                        if (it) {
                            getDetailsOfCoinFromDatabase()
                            toastMessages(R.string.succes)
                            reviewUs()
                            showInterstitialAd()
                            return@observe
                        }
                        toastMessages(R.string.fail)
                        showInterstitialAd()
                    }
                }
            }

            tradeEnum.Sell -> {
                val amount = dataBindingCurrentTrade.coinAmount.text.toString()
                val price = dataBindingCurrentTrade.coinPrice.text.toString()
                if (amount != "" && price != "") {

                    val coinAmount = amount.toDouble()
                    val coinPrice = currentPrice
                    val total = coinAmount * coinPrice

                    viewModel.sellCoin(coinName, coinAmount, total, currentPrice)
                    viewModel.state.observe(viewLifecycleOwner) {
                        if (it) {
                            getDetailsOfCoinFromDatabase(coinName)
                            toastMessages(R.string.succes)
                            showInterstitialAd()
                            return@observe
                        }
                        toastMessages(R.string.fail)
                        showInterstitialAd()
                    }
                }
            }
        }
        viewModel.state
    }

    // this fun for when we click buy button set backGroung color, text color etc.
    fun buyClicked() {
        tradeState = tradeEnum.Buy
        getDetailsOfCoinFromDatabase("USDT")
        dataBindingCurrentTrade.apply {
            Buy.setTextColor(Color.parseColor("#ffffff"))
            Buy.setBackgroundColor(Color.parseColor("#2ebd85"))
            Sell.setTextColor(Color.parseColor("#8e919c"))
            Sell.setBackgroundColor(Color.parseColor("#f5f5f5"))
            doTrade.setBackgroundColor(Color.parseColor("#2ebd85"))
            doTrade.setText(R.string.textBuy)
        }
    }

    // this fun for when we click sell button set backGroung color, text color etc.
    fun sellClicked() {
        tradeState = tradeEnum.Sell
        getDetailsOfCoinFromDatabase(coinName)

        dataBindingCurrentTrade.apply {
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
        upDatePer5Sec()
        super.onResume()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    // this override fun for new total when we changing amount  or when updated price from api
    override fun afterTextChanged(s: Editable?) {
        if (dataBindingCurrentTrade.coinAmount.text.toString() != "" &&
            dataBindingCurrentTrade.coinPrice.text.toString() != ""
        ) {
            val amount = dataBindingCurrentTrade.coinAmount.text.toString().toBigDecimal()
            val price = dataBindingCurrentTrade.coinPrice.text.toString().toBigDecimal()

            if (amount.toDouble() > 0.0 && price.toDouble() > 0.0) {
                val total = (amount * price).toString()
                val coinPrice = if (total.length>10 && total.subSequence(0,10).last().toString() != ".") total.substring(0,10) else  total
                dataBindingCurrentTrade.Total.setText(coinPrice)
            }
            return
        }
            dataBindingCurrentTrade.Total.setText("")
        }

    private fun reviewUs(){
        val reviewUs : ReviewUsI = this
        val reviewUsResult =  reviewUs.reviewUsRequestCompleteListener(activity = requireActivity(), context = requireContext())
        reviewUs.reviewUsStart(activity = requireActivity(), manager = reviewUsResult.first, reviewInfo = reviewUsResult.second)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        when(p0){
            dataBindingCurrentTrade.raise -> {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main){
                        clickListenerInitialize.clickListener(dataBindingCurrentTrade.raise)
                    }
                }
            }
            dataBindingCurrentTrade.minus -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main){
                            clickListenerInitialize.clickListener(dataBindingCurrentTrade.minus)
                        }
                    }
            }
        }
        return false
    }


    private fun showInterstitialAd() {

        adInterstitial?.apply {
            show(requireActivity())
            sharedPreferencesManager(requireContext()).addSharedPreferencesLong("currentTradeInterstitialAd",System.currentTimeMillis()+(30*1000))
        }
    }

    private fun setInterstitialAd() {
        if (System.currentTimeMillis() < 1653298595591) return

        val currentMillis = System.currentTimeMillis()
        val updateTime = sharedPreferencesManager(requireContext()).getSharedPreferencesLong("currentTradeInterstitialAd", currentMillis)
        val delayTime = if (currentMillis >= updateTime) 0L else updateTime - currentMillis

        CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            withContext(Dispatchers.Main){
                val adRequest = AdRequest.Builder().build()
                MobileAds.setRequestConfiguration(RequestConfiguration.Builder().build())

                InterstitialAd.load(requireContext(), "ca-app-pub-2861105825918511/1127322176", adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {}
                    override fun onAdLoaded(interstitialAd: InterstitialAd) { adInterstitial = interstitialAd }
                })

            }
        }

    }

    private fun changeAmounts(currentAmount: Double, quantity: Double, progress: CoinProgress) {
        val newAmount = if (progress == CoinProgress.SUM) currentAmount + quantity else currentAmount - quantity
        val amount = if ( newAmount.toString().length>10 && newAmount.toString().subSequence(0,10).last().toString() != ".") newAmount.toString().substring(0,10) else  newAmount.toString()
        dataBindingCurrentTrade.coinAmount.setText(amount)
    }

    enum class CoinProgress (){
        SUM,MINUS
    }

    private fun setAnimation(context: Context, animation: Int, view: View, animationDuration: Long = 0L ) {
        val animationDetails = AnimationUtils.loadAnimation(context, animation)
        animationDetails.duration = animationDuration
        view.animation = animationDetails
    }
}