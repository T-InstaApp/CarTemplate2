package com.example.cartemplate2.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityRegistrationBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.MainLoginDataModel
import com.example.cartemplate2.datamodel.UserRegisterRequest
import com.example.cartemplate2.datamodel.UserRegistrationResponse
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.AuthViewModel
import com.example.cartemplate2.provider.AuthViewModelFactory
import com.example.cartemplate2.utils.*
import com.example.cartemplate2.utils.StaticValue.SALUTATION_DATA
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class RegistrationActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var progressBar: ProgressDialogLayoutBinding
    override val kodein by kodein()
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolBar
        progressBar = binding.progressBar
        toolbar.txtTopHeading.text = "Registration"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.networkCallListener = this

        binding.autoCompleteSalutation.setAdapter<ArrayAdapter<*>>(
            ArrayAdapter<Any?>(this, R.layout.select_salutation, SALUTATION_DATA)
        )
        binding.autoCompleteSalutation.onFocusChangeListener =
            OnFocusChangeListener { _: View?, hasFocus: Boolean -> if (hasFocus) binding.autoCompleteSalutation.showDropDown() }
        binding.autoCompleteSalutation.setOnTouchListener { _: View?, _: MotionEvent? ->
            binding.autoCompleteSalutation.showDropDown()
            false
        }
        binding.signUp.setOnClickListener { checkValidation() }

        binding.txtSignIn.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun checkValidation() {

        if (binding.autoCompleteSalutation.text.toString().length <= 2) {
            toast("Please select salutation")
            return
        } else if (binding.etUserName.text.toString().length <= 2) {
            toast("Please enter user name")
            return
        } else if (binding.etFirstName.text.toString().length <= 2) {
            toast("Please enter first name")
            return
        } else if (binding.etLastName.text.toString().length <= 2) {
            toast("Please enter last name")
            return
        } else if (binding.etEmail.text.toString().length <= 2) {
            toast("Please enter email")
            return
        } else if (binding.etCode.text.toString().length <= 1) {
            toast("Please enter country code")
            return
        } else if (binding.etMobile.text.toString().length < 9) {
            toast("Please enter mobile number")
            return
        } else if (binding.etPassword.text.toString().length < 7) {
            toast("Please enter your password")
            return
        } else if (binding.etConfirmPassword.text.toString().length < 7) {
            toast("Please enter your confirm password")
            return
        } else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
            toast("Password and confirm password does not match")
            return
        }
        viewModel.mainLogin(
            StaticValue.REST_USER_NAME,
            StaticValue.REST_PASSWORD,
            StaticValue.TEMP_RESTRO_ID
        )
    }

    override fun onStarted() {
        progressBar.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        log("RegistrationActivity", " Failed $message  $type")
        progressBar.progressLayout.notVisible()
        when (type) {
            CommonKey.NO_INTERNET -> {
                showAlert(
                    this,
                    getString(R.string.h_no_internet),
                    getString(R.string.no_internet)
                )
            }
            CommonKey.ERROR_CODE_400 -> {
                showAlert(
                    this,
                    getString(R.string.warning),
                    getString(R.string.login_400)
                )
            }
            else -> {
                showAlert(
                    this,
                    getString(R.string.alert),
                    getString(R.string.no_response)
                )
            }
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {

        log("RegistrationActivity", " Success $type")
        progressBar.progressLayout.notVisible()
        when (type) {
            "mainLogin" -> {
                val data: MainLoginDataModel = dataG as MainLoginDataModel
                val token = data.token
                viewModel.registerUser(
                    token,
                    UserRegisterRequest(
                        binding.etUserName.text.toString().trim(),
                        binding.etEmail.text.toString().trim(),
                        binding.etFirstName.text.toString().trim(),
                        binding.etLastName.text.toString().trim(),
                        binding.etPassword.text.toString().trim(),
                        StaticValue.REST_ID,
                        "N",
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                )
                PreferenceProvider(applicationContext).setStringValue(
                    data.token,
                    PreferenceKey.APP_TOKEN
                )
            }
            "registerUser" -> {
                val data: UserRegistrationResponse = dataG as UserRegistrationResponse
                showAlert(
                    this,
                    getString(R.string.congratulations),
                    getString(R.string.registration_success)
                )
                viewModel.createUser(
                    PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
                    UserRegisterRequest(
                        "",
                        binding.etEmail.text.toString().trim(),
                        binding.etFirstName.text.toString().trim(),
                        binding.etLastName.text.toString().trim(),
                        "",
                        "",
                        "",
                        data.id.toString(),
                        currentTimeStamp(),
                        "extra",
                        binding.autoCompleteSalutation.text.toString().trim(),
                        binding.etCode.text.toString().trim() + binding.etMobile.text.toString()
                            .trim(),
                    )
                )
            }
            "createUser" -> {
                startActivity(
                    Intent(
                        applicationContext,
                        LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }

    }

}