package com.mobikasaba.carlaandroid.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.finance.trade_learn.databinding.CustomAlertBinding
import com.finance.trade_learn.models.CustomAlertFields
import com.finance.trade_learn.utils.DialogManager.configureWindow
import com.finance.trade_learn.utils.NotificationPermissionManager

object AlertDialogCustomBuilder {
        fun showAlertDialog(context: Context,layoutInflater: LayoutInflater,fields: CustomAlertFields,cancellable : Boolean = true, failProgress :() -> Unit = {}, successProgress :() -> Unit = {}): AlertDialog {
            lateinit var alertDialog: AlertDialog
            CustomAlertBinding.inflate(layoutInflater).also { binding ->
                alertDialog = AlertDialog.Builder(context)
                    .setView(binding.root)
                    .setCancelable(cancellable)
                    .create().apply {
                        window?.configureWindow(24f)
                    }
            }.apply {
                customAlertFields = fields
                positiveButton.setOnClickListener {
                    successProgress.invoke()
                    alertDialog.dismiss()
                }
                negativeButton.setOnClickListener {
                    alertDialog.dismiss()
                    failProgress.invoke()
                }
                return alertDialog
            }
        }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun showNotificationPermissionPopup(
        context: Context,
        layoutInflater: LayoutInflater,
        customAlertFields: CustomAlertFields,
        activity: Activity
    ): AlertDialog {
        val dialog = showAlertDialog(
            context = context,
            layoutInflater = layoutInflater,
            fields = customAlertFields
        ) {
            NotificationPermissionManager.requestNotificationPermissionOrOpenSettings(activity = activity)
        }
        return dialog
    }

}