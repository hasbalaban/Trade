package com.finance.trade_learn.utils

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

class newApiKey {
    val API_KEYS = ArrayList<String>()

    fun create(): String {

        val list_of_key = keys()
        val random = (0 until list_of_key.size).random()

        Log.i("random",list_of_key[random])
        return list_of_key[random]
    }

    fun keys(): ArrayList<String> {
        API_KEYS.add("93559813d49113a1760f99eea8b59722edbca326")
        API_KEYS.add("7ace70140d04dbd00b567ca1f52452aa202abbc9")
        API_KEYS.add("df3fcce35c336bc991696621cfae6b4f")
        API_KEYS.add("c9490a962892d1784041d91dfb754ec0f99d153e")
        API_KEYS.add("454aadf31dea285189e4ca4e06a7d0d3d2f5264d")
        API_KEYS.add("502a832ef6f3d0b36068f3ae0175959ba899e677")
        API_KEYS.add("7aaa0ea3f9084f643abf71f0080d01ac2dca8f19")
        API_KEYS.add("8a0f15cae34634cdc03898b4c585a156ca974df0")
        API_KEYS.add("70c78f9c149e61d79c81db0ea7748a262c0cbc63")
        API_KEYS.add("2c19b8fce8aa7eb5dd33bd08063fc33c01cace1d")
        //
        API_KEYS.add("cd8fddbedad5e35c8a61f079a967da4da5f12722")
        API_KEYS.add("d15f4e3ce343e690f6256e08b40ccad1f8b80bbd")
        API_KEYS.add("8fe2e2b8b4bd6a17f77c1cd55b6a6a101757b18a")
        API_KEYS.add("7771fd3c2c02f52a188a0ace81c9288549d0c754")
        API_KEYS.add("40d3ea300e2163ece8433de0e061c1934b3f68bc")
        API_KEYS.add("2478cab5a9aa27072c5f30ce10ba9f6586f074c1")
        API_KEYS.add("ba8e13b1daee074b83b798e260fd153ce959f13d")
        return API_KEYS
    }
}