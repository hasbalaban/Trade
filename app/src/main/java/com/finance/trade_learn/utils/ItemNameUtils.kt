package com.finance.trade_learn.utils


fun solveCoinName(coinName: String): String {
    var resolvedName = ""
    for (i in coinName) {
        if (i.toString() == " ") {
            break
        } else {
            resolvedName += i
        }

    }
    return resolvedName
}