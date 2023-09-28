package com.finance.trade_learn.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.finance.trade_learn.databinding.CustomAlertBinding
import com.finance.trade_learn.models.CustomAlertFields
import com.finance.trade_learn.utils.DialogManager.configureWindow
import kotlin.io.path.fileVisitor

object AlertDialogCustomBuilder {
        private fun showAlertDialog(context: Context, layoutInflater: LayoutInflater, fields: CustomAlertFields, cancellable : Boolean = true, failProgress :() -> Unit = {}, successProgress :() -> Unit = {}): AlertDialog {
            lateinit var alertDialog: AlertDialog
            CustomAlertBinding.inflate(layoutInflater).also { binding ->
                alertDialog = AlertDialog.Builder(context)
                    .setView(binding.root)
                    .setCancelable(cancellable)
                    .create().apply {
                        window?.configureWindow(24f)
                    }
            }.apply {
                fields.imageResId?.let {
                    dialogImage.setImageResource(fields.imageResId)
                }
                fields.title?.let {
                    title.visibility = View.VISIBLE
                    title.text = it
                }
                fields.subtitle?.let {
                    subTitle.visibility = View.VISIBLE
                    subTitle.text = it
                }
                fields.positiveButtonText?.let {
                    positiveButton.visibility = View.VISIBLE
                    positiveButtonText.text = it
                }
                fields.negativeButtonText?.let {
                    negativeButton.visibility = View.VISIBLE
                    negativeButtonText.text = it
                }

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