package com.finance.trade_learn.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.Adapters.SolveCoinName
import com.finance.trade_learn.Adapters.adapter_for_hot_coins
import com.finance.trade_learn.Adapters.adapter_for_populer_coins
import com.finance.trade_learn.R
import com.finance.trade_learn.clickListener.MarketClickListener
import com.finance.trade_learn.databinding.FragmentHomeBinding
import com.finance.trade_learn.enums.enumPriceChange
import com.finance.trade_learn.utils.Ads
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.viewModel.ViewModeHomePage
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class home : Fragment() {

    lateinit var adapterForHotList: adapter_for_hot_coins
    lateinit var adapterForPopulerList: adapter_for_populer_coins
    var viewVisible = false
    lateinit var dataBindingHome: FragmentHomeBinding
    lateinit var viewModelHome: ViewModeHomePage
    var runnable = Runnable { }
    var handler = Handler(Looper.getMainLooper())
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
        startAnimation()
        setAd()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setAd (){
        dataBindingHome.adView.apply {
            loadAd(AdRequest.Builder().build())
            adListener= Ads.listenerAdRequest(dataBindingHome.adView)
        }

    }

    private fun isViewModelIntialize() {
        val state = viewModelHome.isInitialize

        if (state.value!!) {
            viewModelHome.listOfCrypto.observe(viewLifecycleOwner) {list ->

                val random = Random.nextInt(0, list.size - 1)
/*
                with(dataBindingHome.notices){

                    this.setOnClickListener {
                        val coinName = SolveCoinName(list[random].CoinName)
                        sharedPreferencesManager(context)
                            .addSharedPreferencesString("coinName", coinName)
                        findNavController().navigate(R.id.tradePage)

                    }
                    text = list[random].CoinName + " ${list[random].CoinPrice}  chng: ${list[random].CoinChangePercente}"
                }

 */
                adapterForHotList.updateData(list)
                viewModelHome.isInitialize.value = true
            }
            viewModelHome.listOfCryptoForPopular.observe(viewLifecycleOwner) {

                adapterForPopulerList.updateData(it)
            }
        }
    }


    //animation to start
    fun startAnimation() {
        val animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.animation_for_home_view)

        //val imageView = dataBindingHome.notices
        //imageView.animation = animation
    }


    // We Check State of Loading if loading is succesed we Will initialize adapter here
    // then we will set on recycler view


    fun getData() {
        if (viewVisible) {
            //observer state of list of coins
            viewModelHome.state.observe(viewLifecycleOwner, Observer {
                if (it) {
                    if (viewVisible) {
                        viewModelHome.listOfCrypto.observe(
                            viewLifecycleOwner,
                            Observer { list ->
                                list?.let {
                                    adapterForHotList.updateData(it)
                                    if (viewModelHome.isInitialize.value!!){
                                        dataBindingHome.progressBar.visibility=View.INVISIBLE
                                    }


                                    isViewModelIntialize()


                                }


                            })

                        viewModelHome.listOfCryptoForPopular.observe(
                            viewLifecycleOwner,
                            Observer { list ->

                                list?.let {

                                    adapterForPopulerList.updateData(it)
                                }

                            })
                    }
                } else {

                    Log.i("ooooooo", "failed")
                }
            })
        }
    }

    private fun update() {

        runnable = Runnable {
            runBlocking {
                viewModelHome.runGetAllCryptoFromApi()
                getData()
            }
            handler.postDelayed(runnable, 7500)
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


    fun clickToSearch() {
        dataBindingHome.searchCoin.setOnClickListener {
             val action = homeDirections.actionHomeToSearchActivity()
             Navigation.findNavController(it).navigate(action)


        }


    }


}