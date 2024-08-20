package com.finance.trade_learn.view.profile

import com.finance.trade_learn.view.loginscreen.signup.isValidEmail

data class ProfileViewState (
    val isAccountDeleting : Boolean = false,
    var userEmail : String = ""
)
