package com.finance.trade_learn.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.Adapters.adapter_for_hot_coins
import com.finance.trade_learn.Adapters.AdapterForPopulerCoins
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.FragmentHomeBinding
import com.finance.trade_learn.utils.Ads
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.viewModel.ViewModeHomePage
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.*
import java.lang.Runnable

class Home : Fragment() {

    private lateinit var adapterForHotList: adapter_for_hot_coins
    private lateinit var adapterForPopulerList: AdapterForPopulerCoins
    private lateinit var dataBindingHome: FragmentHomeBinding
    private val viewModelHome: ViewModeHomePage by viewModels<ViewModeHomePage> ()
    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 2000L
    private var job : Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBindingHome = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home,
            container, false
        )
        return dataBindingHome.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        adapterForHotList = adapter_for_hot_coins(requireContext(), arrayListOf())
        dataBindingHome.RecyclerViewCoinsOfToday.layoutManager = LinearLayoutManager(requireContext())
        dataBindingHome.RecyclerViewCoinsOfToday.adapter = adapterForHotList

        adapterForPopulerList = AdapterForPopulerCoins(requireContext(), arrayListOf())
        dataBindingHome.RecyclerViewPopulerCoins.adapter = adapterForPopulerList


        clickToSearch()
        clickSendEmailButton()
        //setAd()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setAd() {
        val currentMillis = System.currentTimeMillis()
        val updateTime = sharedPreferencesManager(requireContext()).getSharedPreferencesLong("homePage",currentMillis)
        val delayTime = if (currentMillis >= updateTime) 0L else updateTime-currentMillis
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            withContext(Dispatchers.Main) {
                dataBindingHome.adView.apply {
                    loadAd(AdRequest.Builder().build())
                    adListener = Ads.listenerAdRequest(dataBindingHome.adView,"homePage",requireContext())
                }
            }
        }
    }

    private fun isViewModelInitialize() {
            viewModelHome.listOfCrypto.observe(viewLifecycleOwner) {list ->
                adapterForHotList.updateData(list)
            }
            viewModelHome.listOfCryptoForPopular.observe(viewLifecycleOwner) {

                adapterForPopulerList.updateData(it)
            }
    }

    // We Check State of Loading if loading is successed we Will initialize adapter here
    // then we will set on recycler view
    private fun getData() {
            //observer state of list of coins
            viewModelHome.isLoading.observe(viewLifecycleOwner) {
                if (it.not()) {
                        viewModelHome.listOfCrypto.observe(viewLifecycleOwner) { list ->
                            list?.let {
                                adapterForHotList.updateData(it)
                                timeLoop = 10000
                                dataBindingHome.progressBar.visibility = View.INVISIBLE
                                isViewModelInitialize()
                            }

                        }

                        viewModelHome.listOfCryptoForPopular.value?.let {list ->
                                adapterForPopulerList.updateData(list)
                        }
                    }
            }
    }

    private fun update() {
        runnable = Runnable {
            runBlocking {
                viewModelHome.getAllCryptoFromApi()
                getData()
            }
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }

    override fun onPause() {
        handler.removeCallbacks(runnable)
        job?.cancel()
        super.onPause()
    }

    override fun onResume() {
        update()
        if (viewModelHome.isLoading.value == false) getData()
        super.onResume()
    }


    private fun clickToSearch() {
        dataBindingHome.searchCoin.setOnClickListener {
             val action = HomeDirections.actionHomeToSearchActivity()
             Navigation.findNavController(it).navigate(action)
        }
    }

    private fun clickSendEmailButton(){
        dataBindingHome.sendMail.setOnClickListener {
            composeEmail(arrayOf("learntradeapp@gmail.com"),"A intent or Request")
        }
    }
    private fun composeEmail(addresses: Array<String>, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        try { startActivity(intent)
        }catch (e:Exception){ }
    }
}