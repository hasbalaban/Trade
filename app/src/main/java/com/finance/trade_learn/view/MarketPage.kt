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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.AdapterForMarket
import com.finance.trade_learn.R
import com.finance.trade_learn.utils.Ads
import com.finance.trade_learn.viewModel.ViewModelMarket
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.*
import java.lang.Runnable



var firstSet = true

class MarketPage : Fragment() {


    private lateinit var viewModelMarket: ViewModelMarket
    private lateinit var dataBindingMarket: com.finance.trade_learn.databinding.FragmentMarketPageBinding
    private var viewVisible = true
    private var job: Job? = null

    private var runnable = Runnable { }
    private var handler = Handler(Looper.getMainLooper())
    private var timeLoop = 2000L
    private lateinit var adapter: AdapterForMarket

    override fun onAttach(context: Context) {

        providers()
        super.onAttach(context)
    }

    private fun providers() {

        viewModelMarket = ViewModelProvider(requireActivity())[ViewModelMarket::class.java]
        // update()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        dataBindingMarket = DataBindingUtil.inflate(
            inflater, R.layout.fragment_market_page,
            container, false
        )

        return dataBindingMarket.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        clickToSearch()

        setAdapter()
        isIntializeViewModel()
        setAd()
        super.onViewCreated(view, savedInstanceState)
    }
    private fun setAd (){
        dataBindingMarket.adView.apply {
            loadAd(AdRequest.Builder().build())
            adListener= Ads.listenerAdRequest(dataBindingMarket.adView)
        }

    }
    private fun setAdapter() {
        adapter = AdapterForMarket(requireContext(), arrayListOf())
        dataBindingMarket.RecyclerViewMarket.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        dataBindingMarket.RecyclerViewMarket.layoutManager = layoutManager
    }

    private fun isIntializeViewModel() {

        val status = viewModelMarket.isInitialized
        if (status.value!!) {


            viewModelMarket.listOfCrypto.observe(viewLifecycleOwner) {
                it?.let {
                    adapter.updateData(it)
                    viewModelMarket.isInitialized.value = true
                    dataBindingMarket.progressBar.visibility = View.INVISIBLE
                }

            }
        }

    }

    fun getData() {

        if (viewVisible) {
            //observer state of list of coins
            viewModelMarket.state.observe(viewLifecycleOwner) {
                if (it) {
                    viewModelMarket.listOfCrypto.observe(viewLifecycleOwner) { list ->
                        list?.let {
                            timeLoop = 7000
                            adapter.updateData(list)
                            dataBindingMarket.RecyclerViewMarket
                            dataBindingMarket.progressBar.visibility = View.INVISIBLE
                        }

                    }
                }
            }
        }
    }

    private fun update() {
        job = CoroutineScope(Dispatchers.Main + Job()).launch {
            runnable = object : Runnable {
                override fun run() {
                    viewModelMarket.runGetAllCryptoFromApi()
                    getData()
                    handler.postDelayed(runnable, timeLoop)
                }
            }
            handler.post(runnable)
        }
    }


    override fun onPause() {
        viewVisible = false
        job?.cancel()
        Log.i("onPause", "onPause")
        handler.removeCallbacks(runnable)
        super.onPause()
    }

    override fun onResume() {

        update()
        viewVisible = true
        super.onResume()
    }

    private fun clickToSearch() {
        dataBindingMarket.searchedCoin.setOnClickListener {

            val directions = MarketPageDirections.actionMarketPageToSearchActivity()
            Navigation.findNavController(dataBindingMarket.root).navigate(directions)


        }
    }


}