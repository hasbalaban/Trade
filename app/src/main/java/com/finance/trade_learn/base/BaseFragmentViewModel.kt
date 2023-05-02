package com.finance.trade_learn.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragmentViewModel<VB : ViewBinding, VM : BaseViewModel>(private val inflate : inflate<VB>): Fragment() {

    private var _binding : VB? = null
    val binding get() = _binding!!

    abstract val viewModel : VM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

}

