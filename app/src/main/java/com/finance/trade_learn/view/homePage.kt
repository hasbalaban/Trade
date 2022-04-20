package com.finance.trade_learn.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.whenCreated
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.Adapters.adapter_for_hot_coins
import com.finance.trade_learn.Adapters.adapter_for_populer_coins
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.FragmentHomeBinding
import com.finance.trade_learn.utils.Ads
import com.finance.trade_learn.viewModel.ViewModeHomePage
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.random.Random

class Home : Fragment() {

    private lateinit var adapterForHotList: adapter_for_hot_coins
    private lateinit var adapterForPopulerList: adapter_for_populer_coins
    private var viewVisible = false
    private lateinit var dataBindingHome: FragmentHomeBinding
    private lateinit var viewModelHome: ViewModeHomePage
    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        providers()
        super.onAttach(context)
    }

    private fun providers() {

        viewModelHome = ViewModelProvider(requireActivity())[ViewModeHomePage::class.java]

    }

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
        dataBindingHome.RecyclerViewCoinsOfToday.layoutManager =
            LinearLayoutManager(requireContext())
        dataBindingHome.RecyclerViewCoinsOfToday.adapter = adapterForHotList


        adapterForPopulerList = adapter_for_populer_coins(requireContext(), arrayListOf())
        dataBindingHome.RecyclerViewPopulerCoins.layoutManager =
            GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
        dataBindingHome.RecyclerViewPopulerCoins.adapter = adapterForPopulerList


        clickToSearch()
        setAd()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setAd (){
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000L)
            withContext(Dispatchers.Main){
                dataBindingHome.adView.apply {
                    loadAd(AdRequest.Builder().build())
                    adListener= Ads.listenerAdRequest(dataBindingHome.adView)
                }
            }
        }
    }

    private fun isViewModelInitialize() {
        val state = viewModelHome.isInitialize

        if (state.value!!) {
            viewModelHome.listOfCrypto.observe(viewLifecycleOwner) {list ->
                adapterForHotList.updateData(list)
                viewModelHome.isInitialize.value = true
            }
            viewModelHome.listOfCryptoForPopular.observe(viewLifecycleOwner) {

                adapterForPopulerList.updateData(it)
            }
        }
    }

    // We Check State of Loading if loading is succesed we Will initialize adapter here
    // then we will set on recycler view
    private fun getData() {
        if (viewVisible) {
            //observer state of list of coins
            viewModelHome.state.observe(viewLifecycleOwner) {
                if (it) {
                    if (viewVisible) {
                        viewModelHome.listOfCrypto.observe(viewLifecycleOwner) { list ->
                            list?.let {
                                adapterForHotList.updateData(it)
                                timeLoop = 7500
                                if (viewModelHome.isInitialize.value!!) {
                                    dataBindingHome.progressBar.visibility = View.INVISIBLE
                                }
                                isViewModelInitialize()
                            }

                        }

                        viewModelHome.listOfCryptoForPopular.observe(viewLifecycleOwner) { list ->
                            list?.let {
                                adapterForPopulerList.updateData(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun update() {

        runnable = Runnable {
            runBlocking {
                viewModelHome.runGetAllCryptoFromApi()
                getData()
            }
            handler.postDelayed(runnable, timeLoop)
        }
        handler.post(runnable)
    }

    override fun onPause() {
        viewVisible = false
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    override fun onResume() {
        viewVisible = true
        update()
        if (viewModelHome.state.value == true) getData()
        super.onResume()
    }


    private fun clickToSearch() {
        dataBindingHome.searchCoin.setOnClickListener {
             val action = HomeDirections.actionHomeToSearchActivity()
             Navigation.findNavController(it).navigate(action)
        }
    }
}