package com.finance.trade_learn.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
// open comment line after migration android 13 complete
*/
object NotificationPermissionManager {

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermissionOrOpenSettings(activity: Activity){
        if (canAskNotificationPermission(activity)){
            requestNotificationPermission(activity)
            return
        }
        openSettingToGetNotificationPermission(activity, activity.packageName)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(activity: Activity){
        val requestedPermissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        ActivityCompat.requestPermissions(activity, requestedPermissions, Constants.POST_NOTIFICATION)
    }

    private fun openSettingToGetNotificationPermission(activity: Activity, packageName : String){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        activity.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasNotificationPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun canAskNotificationPermission(activity: Activity) : Boolean = activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)

}