package com.example.cartemplate2.ui.auth

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
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
import com.example.cartemplate2.MainActivity
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityGuestLoginBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.MobileVerifyResponse
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.AuthViewModel
import com.example.cartemplate2.provider.AuthViewModelFactory
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class GuestLoginActivity : AppCompatActivity(), NetworkCallListener, KodeinAware {
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var binding: ActivityGuestLoginBinding
    private lateinit var toolbar: ToolbarBinding
    lateinit var progressLayout: ProgressDialogLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuestLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressLayout = binding.progressBar

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        viewModel.networkCallListener = this
        toolbar.txtTopHeading.text = "Guest Login"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
            finish()
        }
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            intent.putExtra("callFrom", "GuestLogin")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }
        binding.btnSendOtp.setOnClickListener {
            if (binding.etCode.text.toString().length <= 2) {
                toast("Please enter country code")
                return@setOnClickListener
            } else if (binding.etMobile.text.toString().length < 9) {
                toast("Please enter mobile no.")
                return@setOnClickListener
            }
            viewModel.generateOtp(
                binding.etCode.text.toString().trim() + binding.etMobile.text.toString().trim()
            )
        }
    }

    override fun onStarted() {
        progressLayout.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressLayout.progressLayout.notVisible()
        when (type) {
            "validation" -> binding.topLayout.snakeBar(message)
            CommonKey.ERROR_CODE_400 -> showAlert(
                this,
                getString(R.string.warning),
                getString(R.string.no_response)
            )
            else -> showAlert(
                this,
                getString(R.string.alert),
                getString(R.string.no_response)
            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressLayout.progressLayout.notVisible()
        if (type == "generateOtp") {
            verifyOTP()
        } else if (type == "verifyOTP") {
            val data: MobileVerifyResponse = dataG as MobileVerifyResponse
            PreferenceProvider(applicationContext).setStringValue(
                data.id!!.toString(),
                PreferenceKey.USER_ID
            )
            PreferenceProvider(applicationContext).setStringValue(
                data.token!!,
                PreferenceKey.APP_TOKEN
            )
            PreferenceProvider(applicationContext).setBooleanValue(true, PreferenceKey.LOGIN_STATUS)
            PreferenceProvider(applicationContext).setStringValue("2", PreferenceKey.USER_PROFILE)

            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun verifyOTP() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_input_field)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val edtText = dialog.findViewById<EditText>(R.id.etInputData)
        val txtInputHeading = dialog.findViewById<TextView>(R.id.txtInputHeading)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)

        edtText.hint = getString(R.string.enter_otp)
        txtInputHeading.text = getString(R.string.enter_otp)
        edtText.inputType = InputType.TYPE_CLASS_NUMBER
        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.logo))
            .into(imgAppLogo)

        (dialog.findViewById<View>(R.id.btnSubmit) as Button).setOnClickListener { view: View? ->
            dialog.dismiss()
            if (edtText.text.isNullOrEmpty() || edtText.text.toString().length < 2) {
                toast(getString(R.string.enter_otp))
            } else {
                viewModel.verifyOTP(
                    edtText.text.toString(),
                    binding.etCode.text.toString().trim(), binding.etMobile.text.toString().trim()
                )
            }
        }

        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }
}