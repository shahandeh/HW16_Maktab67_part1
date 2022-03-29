package com.example.hw16_maktab67_part1.ui.main

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.ui.model.LoginInterface
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class LogInAlertDialog(
    private val context: Context,
    private val layout: View,
    private val loginAlertInterface: LoginInterface
) {
    private lateinit var alertDialog: AlertDialog

    fun loginDialog() {
        MaterialAlertDialogBuilder(context)
            .setView(layout)
            .show()
            .apply {
                alertDialog = this

                layout.findViewById<MaterialButton>(R.id.login_account_button).setOnClickListener {
                    login()
                }

                layout.findViewById<MaterialButton>(R.id.sign_up).setOnClickListener {
                    signUp()
                }

                layout.findViewById<MaterialButton>(R.id.create_account_button).setOnClickListener {
                    goToCreateAccount()
                }

                layout.findViewById<MaterialButton>(R.id.signup_back_button).setOnClickListener {
                    backToSignIn()
                }
            }
    }

    private fun login() {
        val username = layout.findViewById<TextInputEditText>(R.id.get_username).text.toString()
        val password = layout.findViewById<TextInputEditText>(R.id.get_password).text.toString()
        loginAlertInterface.login(username, password)
    }

    private fun signUp() {
        val name = layout.findViewById<TextInputEditText>(R.id.set_name)
        val username = layout.findViewById<TextInputEditText>(R.id.set_username)
        val email = layout.findViewById<TextInputEditText>(R.id.set_email)
        val password = layout.findViewById<TextInputEditText>(R.id.set_password)

        if (
            username.text.toString().isNotBlank() &&
            name.text.toString().isNotBlank() &&
            email.text.toString().isNotBlank() &&
            password.text.toString().isNotBlank()
        ) {
            loginAlertInterface.signUp(
                name.text.toString(),
                username.text.toString(),
                email.text.toString(),
                password.text.toString()
            )
        }
    }

    private fun backToSignIn() {
        layout.findViewById<ConstraintLayout>(R.id.sign_in_layout).visibility =
            View.VISIBLE
        layout.findViewById<ConstraintLayout>(R.id.signup_layout).visibility = View.GONE
    }

    private fun goToCreateAccount() {
        layout.findViewById<ConstraintLayout>(R.id.sign_in_layout).visibility =
            View.GONE
        layout.findViewById<ConstraintLayout>(R.id.signup_layout).visibility =
            View.VISIBLE
    }

    fun loginDismiss() {
        alertDialog.dismiss()
    }

}