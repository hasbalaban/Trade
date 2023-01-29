package com.finance.trade_learn.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ItemOfSearchBinding
import com.finance.trade_learn.models.SearchCoinItem
import com.finance.trade_learn.models.SearchedModel
import com.finance.trade_learn.models.coin_gecko.CoinInfoList
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.view.SearchFragmentDirections

class AdapterForSearchCoin(
    val list: ArrayList<SearchCoinItem>,
    val context: Context
) :
    RecyclerView.Adapter<AdapterForSearchCoin.viewHolder>() {

    class viewHolder(val view: ItemOfSearchBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemOfSearchBinding>(
            inflater,
            R.layout.item_of_search,
            parent,
            false
        )

        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        when (val item = list[position]) {
            is CoinInfoList -> {
                holder.view.coin = SearchedModel(item.name, "")
                holder.view.CoinText.setOnClickListener {
                    sharedPreferencesManager(context)
                        .addSharedPreferencesString("coinName", item.id)

                    val directions = SearchFragmentDirections.actionSearchActivityToTradePage()
                    Navigation.findNavController(holder.view.root).navigate(directions)
                }
                item.id
            }
            else -> {
                holder.view.coin = SearchedModel(item.coinId, "")
                holder.view.CoinText.setOnClickListener {
                    sharedPreferencesManager(context)
                        .addSharedPreferencesString("coinName", item.coinId)

                    val directions = SearchFragmentDirections.actionSearchActivityToTradePage()
                    Navigation.findNavController(holder.view.root).navigate(directions)
                }
                list[position].coinId
            }
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun updateAdapterSearchCoin(newList: List<SearchCoinItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()

    }

    fun navigateManager(coinName: String) {
        //  val action:
    }
}