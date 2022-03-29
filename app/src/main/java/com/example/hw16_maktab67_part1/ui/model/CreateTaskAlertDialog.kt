package com.example.hw16_maktab67_part1.ui.model

import android.content.Context
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.database.TaskStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CreateTaskAlertDialog(
    private val layout: View,
    private val context: Context,
    private val pictureDialogLayout: View,
    private val createTaskInterface: CreateTaskInterface
) {

    private lateinit var alertDialog: AlertDialog


///////////////////////////////////////private fun save/////////////////////////////////////////////

    private val radioGroup: RadioGroup = layout.findViewById(R.id.task_layout_add_radioGroup)
    private val radioButtonId = layout.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
    private val title: TextInputEditText = layout.findViewById(R.id.task_layout_add_title)
    private val description: TextInputEditText = layout.findViewById(R.id.task_layout_add_description)


    fun createDialog(){
        MaterialAlertDialogBuilder(context)
            .setView(layout)
            .show()
            .apply {

                alertDialog = this

                layout.findViewById<MaterialButton>(R.id.task_layout_add_time_picker).setOnClickListener {
                    timePicker()
                }

                layout.findViewById<MaterialButton>(R.id.task_layout_add_date_picker).setOnClickListener {
                    datePicker()
                }

                layout.findViewById<MaterialButton>(R.id.task_layout_add_save_button).setOnClickListener {
                    save()
                }

                layout.findViewById<MaterialButton>(R.id.task_layout_add_cancel_button).setOnClickListener {
                    this.dismiss()
                }

                layout.findViewById<MaterialButton>(R.id.task_layout_add_picture_button).setOnClickListener {
                    getPictureDialog()
                }
            }
    }

    private fun timePicker() {
        createTaskInterface.timePicker()
    }

    private fun datePicker() {
        createTaskInterface.datePicker()
    }

    private fun save(){
        val title = title.text.toString()
        val description = description.text.toString()
        val radioGroupIdTemp = radioGroup.checkedRadioButtonId
        val radioButton = layout.findViewById<RadioButton>(radioGroupIdTemp)
        val taskStatus : TaskStatus =
            when(radioButton.text.toString()) {
                "Todo" -> TaskStatus.TODO
                "Doing" -> TaskStatus.DOING
                else -> TaskStatus.DONE
            }
        createTaskInterface.save(title, description, taskStatus)

    }

    private fun getPictureDialog() {
        MaterialAlertDialogBuilder(context)
            .setView(pictureDialogLayout)
            .show()
            .apply {
                pictureDialogLayout.findViewById<MaterialButton>(R.id.pick_image_layout_gallery)
                    .setOnClickListener {
                        createTaskInterface.pictureFromGallery()

                    }
                pictureDialogLayout.findViewById<MaterialButton>(R.id.pick_image_layout_camera)
                    .setOnClickListener {
                        createTaskInterface.pictureFromCamera()

                    }
            }
    }

    fun alertDialogDismiss(){
        alertDialog.dismiss()
    }



}