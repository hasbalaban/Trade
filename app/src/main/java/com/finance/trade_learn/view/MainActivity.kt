package com.finance.trade_learn.view


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.finance.trade_learn.R
import com.finance.trade_learn.databinding.ActivityMainBinding
import com.finance.trade_learn.utils.sharedPreferencesManager
import com.finance.trade_learn.utils.testWorkManager
import com.finance.trade_learn.viewModel.ViewModelMarket
import com.finance.trade_learn.viewModel.viewModelUtils
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smartlook.sdk.smartlook.Smartlook
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var controller: NavController
    private lateinit var dataBindingMain: ActivityMainBinding
    private lateinit var viewModelUtils: viewModelUtils
    private lateinit var viewModelMarket: ViewModelMarket


    private lateinit var firestore: FirebaseFirestore

    // val disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        providers()
        super.onCreate(savedInstanceState)
        dataBindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Smartlook.setupAndStartRecording("49af8b0bc2a7ef077d215bfde0b330a2269559fc")

        bottomNavigationItemClickListener()
        isOneEntering()
        firebaseSave()


        MobileAds.initialize(this) {}
    }


    private fun providers() {
        viewModelMarket = ViewModelProvider(this).get(ViewModelMarket::class.java)
    }


    // to navigate according click in fragment
    private fun bottomNavigationItemClickListener() {

        controller = findNavController(R.id.fragmentContainerView)
        dataBindingMain.options.setupWithNavController(controller)

    }

    //check is first entering or no ? // if it's first time add 1000 dollars
    private fun isOneEntering() {
        viewModelUtils = viewModelUtils()
        val state = viewModelUtils.isOneEntering(this)
        if (state) {
            // these functions just for test
            testWorkManager()
            Log.i("first", "this is first Entering")

            val deviceId = UUID.randomUUID()
            sharedPreferencesManager(this).addSharedPreferencesString(
                "deviceId",
                deviceId.toString()
            )


        } else
            Log.i("firstNot", "this is not first Entering")

    }



    fun firebaseSave() {

        firestore = Firebase.firestore
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val deviceID = sharedPreferencesManager(this).getSharedPreferencesString("deviceId", "0")
        val openAppDetails = hashMapOf(
            "open" to "1",
            "time" to currentDate,
            "country" to Locale.getDefault().country,
            "deviceID" to deviceID
        )
        if (deviceID != "057eea2e-396c-4117-b5d4-782b247000f9") {// this condotion will be delete
            firestore.collection("StartApp").add(openAppDetails).addOnSuccessListener {
            }.addOnFailureListener {

            }
        }
    }


}