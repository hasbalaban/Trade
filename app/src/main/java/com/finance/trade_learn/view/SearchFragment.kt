package com.finance.trade_learn.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.trade_learn.Adapters.adapter_for_search_coin
import com.finance.trade_learn.data.Cache_Data
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.FragmentSettingPageBinding
import com.finance.trade_learn.databinding.SearchFragmentBinding
import kotlinx.coroutines.*
import java.util.*

class SearchFragment : Fragment(), TextWatcher {


    var job = arrayListOf<Job>()
    private lateinit var adapter: adapter_for_search_coin
    private lateinit var binding: SearchFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<SearchFragmentBinding>(inflater, R.layout.search_fragment,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // setTitle(R.string.page_name)
        requireActivity().setTheme(R.style.thema_search)
        setup()
        super.onViewCreated(view, savedInstanceState)
    }


    fun setup() {
        adapter = adapter_for_search_coin(arrayListOf(), requireContext())
        setAdapter()
        binding.searchCoin.addTextChangedListener(this)
    }

    fun setAdapter() {
        binding.searchedCoins.layoutManager = LinearLayoutManager(requireContext())
        binding.searchedCoins.adapter = adapter
    }

    fun getEqualCoins(): List<String> {
        val listCoin = Cache_Data().coinsName()
        val textOfQuery = binding.searchCoin.text.toString()
            .uppercase(Locale.getDefault())

        val queryList = listCoin.filter {
            it.startsWith(textOfQuery)
        }

        return queryList

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s.toString()!= s.toString().uppercase(Locale.getDefault())){

            binding.searchCoin.setText(binding.searchCoin.text.toString()
                .uppercase(Locale.getDefault()))
        }

        job.clear()
    }

    override fun afterTextChanged(s: Editable?) {
        if (binding.searchCoin.text.toString() != "") {

            val newJob = CoroutineScope(Job() + Dispatchers.IO).launch {
                val newList = getEqualCoins()
                withContext(Dispatchers.Main) {
                    adapter.updateAdapterSearchCoin(newList)
                }
            }
            job.add(newJob)
        }

        val edittextCursorPosition =  binding.searchCoin.length()
        binding.searchCoin.setSelection(edittextCursorPosition)
    }



}
