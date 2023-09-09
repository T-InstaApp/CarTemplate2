package com.example.cartemplate2.ui.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cartemplate2.MainActivity2
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityLoginBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.LoginDataModel
import com.example.cartemplate2.datamodel.MainLoginDataModel
import com.example.cartemplate2.datamodel.UserIDResponse
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.AuthViewModel
import com.example.cartemplate2.provider.AuthViewModelFactory
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var progressBinding: ProgressDialogLayoutBinding
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolBar
        progressBinding = binding.progress
        toolbar.txtTopHeading.text = "Login"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.networkCallListener = this

        binding.txtSignUp.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    RegistrationActivity::class.java
                )
            )
        }

        binding.btnLogin.setOnClickListener { checkValidation() }

        binding.txtForgotUserName.setOnClickListener {
            forgotUserPassword("User")
        }
        binding.txtForgotPassword.setOnClickListener {
            forgotUserPassword("Password")
        }
    }

    private fun checkValidation() {
        if (binding.etUserName.text.toString().length < 2) {
            toast("Please enter user name")
            return
        } else if (binding.etPassword.text.toString().length < 7) {
            toast("Please enter password")
            return
        }
        val data = LoginDataModel(
            binding.etUserName.text.toString(),
            binding.etPassword.text.toString(),
            StaticValue.REST_ID
        )
        viewModel.login(data)

    }

    override fun onStarted() {
        progressBinding.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressBinding.progressLayout.notVisible()
        log("Login", " Failed $type")
        when (type) {
            CommonKey.NO_INTERNET -> {
                showAlert(
                    this,
                    getString(R.string.h_no_internet),
                    getString(R.string.no_internet)
                )
            }
            CommonKey.ERROR_CODE_401 -> {
                showAlert(
                    this,
                    getString(R.string.h_login_401),
                    getString(R.string.login_401)
                )
            }
            CommonKey.ERROR_CODE_403 -> {
                if (message.contains(getString(R.string.no_restro_access))) {
                    RestroAccess()
                } else
                    showAlert(
                        this,
                        getString(R.string.h_login_403),
                        message
                    )
            }
            else -> showAlert(
                this,
                getString(R.string.alert),
                message
                //  getString(R.string.no_response)
            )
        }

    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressBinding.progressLayout.notVisible()
        log("RegistrationActivity", " Success $type")
        if (type == "login") {
            val data: MainLoginDataModel = dataG as MainLoginDataModel
            PreferenceProvider(applicationContext).setStringValue(
                data.token,
                PreferenceKey.APP_TOKEN
            )
            PreferenceProvider(applicationContext).setIntValue(
                data.id.toInt(),
                PreferenceKey.USER_ID
            )
            PreferenceProvider(applicationContext).setBooleanValue(
                true,
                PreferenceKey.LOGIN_STATUS
            )
            PreferenceProvider(applicationContext).setStringValue(
                "REGISTERED",
                PreferenceKey.LOGIN_TYPE
            )
            PreferenceProvider(applicationContext).setStringValue(
                "1",
                PreferenceKey.USER_PROFILE
            )
            val intent = Intent(this, MainActivity2::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
        if (type == "mainLogin") {
            val data: MainLoginDataModel = dataG as MainLoginDataModel
            PreferenceProvider(applicationContext).setStringValue(
                data.token,
                PreferenceKey.APP_TOKEN
            )
            viewModel.getUserID(data.token, binding.etUserName.text.toString())
        }
        if (type == "getUserID") {
            val data: UserIDResponse = dataG as UserIDResponse
            viewModel.insertUser(
                PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
                data.results?.get(0)?.id.toString(),
                PreferenceProvider(applicationContext).getStringValue(StaticValue.REST_ID)!!
            )
        }
        if (type == "insertUser") {
            viewModel.checkLogin(
                StaticValue.REST_ID,
                binding.etUserName.text.toString(),
                binding.etPassword.text.toString()
            )
        }
        if (type == "insertUser") {
            showAlert(
                this,
                getString(R.string.congratulations),
                getString(R.string.username_reset_success)
            )
        }
        if (type == "insertUser") {
            showAlert(
                this,
                getString(R.string.congratulations),
                getString(R.string.password_reset_success)
            )
        }
    }


    private fun RestroAccess() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.custom_dialog)
        val edtEmail = dialog.findViewById<TextView>(R.id.txtMsg)
        val button = dialog.findViewById<Button>(R.id.btnOk)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.ic_login))
            .into(imgAppLogo)
        button.text = getString(R.string.ok)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        edtEmail.text = resources.getString(R.string.confirm_user)
        (dialog.findViewById<View>(R.id.btnOk) as Button).setOnClickListener {
            viewModel.mainLogin(
                StaticValue.REST_USER_NAME,
                StaticValue.REST_PASSWORD,
                StaticValue.TEMP_RESTRO_ID
            )
        }
        button.setOnClickListener { dialog.dismiss() }
        imgAppLogo.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }


    @SuppressLint("SetTextI18n")
    private fun forgotUserPassword(type: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_input_field)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<EditText>(R.id.etInputData)
        val txtInputHeading = dialog.findViewById<TextView>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)

        if (type == "User") {
            edtText.hint = getString(R.string.enter_your_email)
            txtInputHeading.text = getString(R.string.email)
            txtHeading.text = "Reset your username"
        } else {
            edtText.hint = getString(R.string.enter_your_user_name)
            txtInputHeading.text = getString(R.string.user_name)
            txtHeading.text = "Reset your password"
        }

        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.logo))
            .into(imgAppLogo)

        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener {
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                if (type == "User")
                    toast(getString(R.string.enter_your_email))
                else
                    toast(getString(R.string.enter_your_user_name))
            } else {
                dialog.dismiss()
                if (type == "User")
                    viewModel.resetUserName(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
                    )
                else if (type == "Password")
                    viewModel.resetPassword(
                        PreferenceProvider(applicationContext).getStringValue(
                            PreferenceKey.APP_TOKEN
                        )!!, edtText.text.toString()
                    )
            }
        }

        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }
}