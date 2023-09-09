package com.example.cartemplate2.ui.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityProfileBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.ChangePassword
import com.example.cartemplate2.datamodel.ProfileDataRequest
import com.example.cartemplate2.datamodel.ProfileResponse
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.AuthViewModel
import com.example.cartemplate2.provider.AuthViewModelFactory
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware, NetworkCallListener {
    override val kodein by kodein()
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: ToolbarBinding
    private lateinit var progressBinding: ProgressDialogLayoutBinding
    private lateinit var viewModel: AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        viewModel.networkCallListener = this

        toolbar = binding.toolBar
        progressBinding = binding.progress
        toolbar.txtTopHeading.text = "Profile"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)

        if (PreferenceProvider(requireContext()).getStringValue(PreferenceKey.USER_PROFILE) == "2") {
            binding.txtChangePassword.visibility = View.GONE
        }

        viewModel.getUserProfile(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID).toString()
        )

        binding.updateProfile.setOnClickListener {
            viewModel.onUpdateUserProfile(
                "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
                PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID).toString(),
                ProfileDataRequest(
                    binding.autoCompleteSalutation.text.toString(),
                    binding.etFirstName.text.toString(),
                    binding.etLastName.text.toString(),
                    binding.etUserName.text.toString(),
                    binding.etEmail.text.toString(),
                    currentTimeStamp(),
                    binding.etCode.text.toString() + binding.etMobile.text.toString()
                )
            )
        }

        binding.txtChangePassword.setOnClickListener { changePassword() }
    }

    override fun onStarted() {
        progressBinding.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressBinding.progressLayout.notVisible()
        when (type) {
            CommonKey.ERROR_CODE_401 -> {
                dialogSessionExpire(requireContext())
            }
            else -> showAlert(requireActivity(), getString(R.string.alert), message)
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressBinding.progressLayout.notVisible()
        log("ProfileFragment", " Success $type  $dataG")
        when (type) {
            "onUpdateUserProfile" -> {
                viewModel.updateCustomerProfile(
                    "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
                    PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID)
                        .toString(),
                    ProfileDataRequest(
                        binding.autoCompleteSalutation.text.toString().trim(),
                        binding.etFirstName.text.toString().trim(),
                        binding.etLastName.text.toString().trim(),
                        binding.etUserName.text.toString().trim(),
                        binding.etEmail.text.toString().trim(),
                        currentTimeStamp(),
                        binding.etCode.text.toString().trim() + binding.etMobile.text.toString()
                            .trim()
                    )
                )
            }
            "updateCustomerProfile" -> {
            }
            "changePassword" -> {
                dialog.dismiss()
                showAlert(
                    requireActivity(),
                    getString(R.string.congratulations),
                    getString(R.string.password_change_successfully)
                )
            }
            else -> {
                val data: ProfileResponse = dataG as ProfileResponse
                binding.profileData = data.results?.get(0)
            }
        }
    }

    private fun changePassword() {
        dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_pasword)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val tool_bar = dialog.findViewById<RelativeLayout>(R.id.tool_bar)
        val imgCancel = dialog.findViewById<ImageView>(R.id.imgCancel)
        val imgAppLogo = dialog.findViewById<ImageView>(R.id.imgLogo)
        val etOldPassword = dialog.findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = dialog.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialog.findViewById<EditText>(R.id.etConfirmPassword)
        val btnSubmit = dialog.findViewById<AppCompatButton>(R.id.btnSubmit)

        Glide.with(requireContext())
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.logo))
            .into(imgAppLogo)
        btnSubmit.setOnClickListener {

            if (etOldPassword.text.isNullOrEmpty() || etOldPassword.text.toString().length < 5) {
                tool_bar.snakeBar("Please enter old password")
                return@setOnClickListener
            } else if (etNewPassword.text.isNullOrEmpty()) {
                tool_bar.snakeBar("Please enter a new password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString().length < 7) {
                tool_bar.snakeBar("Password must be at least 8 characters")
                return@setOnClickListener
            } else if (etConfirmPassword.text.isNullOrEmpty() || etConfirmPassword.text.toString().length < 7) {
                tool_bar.snakeBar("Please enter the confirmation password")
                return@setOnClickListener
            } else if (etNewPassword.text.toString() != etConfirmPassword.text.toString()) {
                tool_bar.snakeBar("New password and confirm password do not match")
                return@setOnClickListener
            }
            viewModel.changePassword(
                "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
                ChangePassword(
                    etOldPassword.text.toString(),
                    binding.etUserName.text.toString(),
                    etNewPassword.text.toString(),
                    etConfirmPassword.text.toString()
                )
            )
        }

        imgCancel.setOnClickListener { dialog.dismiss() }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
