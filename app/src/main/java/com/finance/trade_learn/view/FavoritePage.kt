package com.finance.trade_learn.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentFavoritePageBinding
import com.finance.trade_learn.viewModel.SearchCoinViewModel


class FavoritePage(override val viewModel: SearchCoinViewModel) :  BaseFragmentViewModel<FragmentFavoritePageBinding, SearchCoinViewModel>(FragmentFavoritePageBinding::inflate) {

    lateinit var dataBindingFavorite: FragmentFavoritePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBindingFavorite = FragmentFavoritePageBinding.inflate(inflater, container, false)
        return  dataBindingFavorite.root
    }
}