package com.finance.trade_learn.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.finance.trade_learn.R
import com.finance.trade_learn.base.BaseFragmentViewModel
import com.finance.trade_learn.databinding.FragmentMarketPageBinding
import com.finance.trade_learn.databinding.FragmentSettingPageBinding
import com.finance.trade_learn.viewModel.ViewModelMarket


class SettingPage(override val viewModel: ViewModelMarket) : BaseFragmentViewModel<FragmentMarketPageBinding, ViewModelMarket>(
    FragmentMarketPageBinding::inflate) {

    lateinit var dataBindingSetting: FragmentSettingPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBindingSetting= FragmentSettingPageBinding.inflate(inflater, container,false)
        return  dataBindingSetting.root
    }


}