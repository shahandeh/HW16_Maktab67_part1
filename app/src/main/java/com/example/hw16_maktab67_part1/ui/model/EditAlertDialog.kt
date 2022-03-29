package com.example.hw16_maktab67_part1.ui.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.database.Task
import com.example.hw16_maktab67_part1.database.TaskStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class EditAlertDialog(
    private val task: Task,
    private val context: Context,
    private val editTaskLayout: View,
    private val editAlertInterface: EditTaskInterface
) {
    private val showConstraintLayout =
        editTaskLayout.findViewById<ConstraintLayout>(R.id.task_layout_edit_show)
    private val showTitle = editTaskLayout.findViewById<TextView>(R.id.task_layout_edit_show_edit_title)
    private val showDescription =
        editTaskLayout.findViewById<TextView>(R.id.task_layout_edit_show_edit_description)
    private val showTime = editTaskLayout.findViewById<TextView>(R.id.task_layout_edit_show_edit_time)
    private val showDate = editTaskLayout.findViewById<TextView>(R.id.task_layout_edit_show_edit_date)
    private val showTaskStatus = editTaskLayout.findViewById<TextView>(R.id.task_layout_edit_show_edit_task_status)
    private val showImage = editTaskLayout.findViewById<ImageView>(R.id.task_layout_edit_show_edit_image_view)
    private val showDeleteButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_show_edit_delete_button)
    private val showEditButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_show_edit_button)
    private val showShareButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_show_edit_share_button)

    private val editConstraintLayout =
        editTaskLayout.findViewById<ConstraintLayout>(R.id.task_layout_edit_edit)
    private val editTitle: TextView =
        editTaskLayout.findViewById(R.id.task_layout_edit_edit_title)
    private val editDescription: TextInputEditText =
        editTaskLayout.findViewById(R.id.task_layout_edit_edit_description)
    private val editDatePicker =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_edit_date_picker)
    private val editTimePicker =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_edit_time_picker)
    private val editRadioGroup =
        editTaskLayout.findViewById<RadioGroup>(R.id.task_layout_edit_edit_radioGroup)
    private val editCancelButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_edit_cancel_button)
    private val editPictureButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_edit_picture_button)
    private val editSaveButton =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_edit_save_button)

    private val pictureSelectionLayout =
        editTaskLayout.findViewById<ConstraintLayout>(R.id.task_layout_edit_picture_selection)
    private val pictureSelectionGallery =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_picture_selection_gallery)
    private val pictureSelectionCamera =
        editTaskLayout.findViewById<MaterialButton>(R.id.task_layout_edit_picture_selection_camera)

    private lateinit var alertDialog: AlertDialog

////////////////////////////////////////////////////////////////////////////////////////////////////

    fun showTaskDialog() {
        showConstraintLayout.visibility = View.VISIBLE
        editConstraintLayout.visibility = View.GONE
        pictureSelectionLayout.visibility = View.GONE

        MaterialAlertDialogBuilder(context)
            .setView(editTaskLayout)
            .show()
            .apply {
                alertDialog = this
                showTitle.text = task.title
                showDescription.text = task.description
                showTime.text = task.time
                showDate.text = task.date
                showTaskStatus.text = task.taskStatus.toString()
                if (task.uri != null) showImage.glide(task.uri)
                else showImage.visibility = View.GONE

                showDeleteButton.setOnClickListener { deleteButtonClicked() }

                showEditButton.setOnClickListener { editButtonClicked() }

                showShareButton.setOnClickListener {
                    editAlertInterface.share()
                }
            }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun editButtonClicked() {
        editTaskDialog()
    }

    private fun deleteButtonClicked() {
        editAlertInterface.deleteTask()
        alertDialog.dismiss()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun editTaskDialog() {
        showConstraintLayout.visibility = View.GONE
        editConstraintLayout.visibility = View.VISIBLE
        pictureSelectionLayout.visibility = View.GONE

        editTitle.hint = task.title
        editDescription.hint = task.description

        editDatePicker.setOnClickListener {
            editAlertInterface.datePicker()
        }

        editTimePicker.setOnClickListener {
            editAlertInterface.timePicker()
        }

        editPictureButton.setOnClickListener {
            getPictureDialog()
        }

        editSaveButton.setOnClickListener {
            saveTask()
        }

        editCancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun saveTask() {
        val description = editDescription.text.toString()
        val taskStatus =
            when (editTaskLayout.findViewById<RadioButton>(editRadioGroup.checkedRadioButtonId).text) {
                "Todo" -> TaskStatus.TODO
                "Doing" -> TaskStatus.DOING
                else -> TaskStatus.DONE
            }

        editAlertInterface.save(description, taskStatus)
    }

    private fun getPictureDialog() {
        showConstraintLayout.visibility = View.GONE
        editConstraintLayout.visibility = View.GONE
        pictureSelectionLayout.visibility = View.VISIBLE

        pictureSelectionGallery.setOnClickListener {
            editAlertInterface.pictureFromGallery()
        }
        pictureSelectionCamera.setOnClickListener {
            editAlertInterface.pictureFromCamera()
        }
    }

}