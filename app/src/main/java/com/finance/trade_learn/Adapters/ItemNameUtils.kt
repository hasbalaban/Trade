package com.finance.trade_learn.Adapters


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