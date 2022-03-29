package com.example.hw16_maktab67_part1.ui.model

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.hw16_maktab67_part1.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RemoveAllTaskAlertDialog(
    private val context: Context,
    private val layout: View,
    private val removeAllTaskAlertDialogInterface: RemoveAllTaskAlertDialogInterface
) {

    private lateinit var alertDialog: AlertDialog

    fun create(){
        val removeAllButton = layout.findViewById<MaterialButton>(R.id.remove_all_task_dialog_remove_all)
        val cancelButton = layout.findViewById<MaterialButton>(R.id.remove_all_task_dialog_cancel)
        MaterialAlertDialogBuilder(context)
            .setView(layout)
            .show()
            .apply {
                alertDialog = this
                removeAllButton.setOnClickListener { removeAllTaskAlertDialogInterface.removeAllTask() }
                cancelButton.setOnClickListener { this.dismiss() }
            }
    }

    fun alertDialogDismiss(){
        alertDialog.dismiss()
    }

}