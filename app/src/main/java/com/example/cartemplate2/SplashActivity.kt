package com.example.cartemplate2


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cartemplate2.databinding.ActivitySplashBinding
import com.example.cartemplate2.datamodel.CompanyData
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.HomeViewModel
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    override val kodein by kodein()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        setContentView(view)
        //  val imageUrl=PreferenceProvider(applicationContext).getStringValue(PreferenceKey.IMAGE_LOGO)
        val imageUrl =
            "https://www.freepnglogos.com/uploads/company-logo-png/company-logo-transparent-png-19.png"

        Glide.with(applicationContext)
            .load(imageUrl)
            .into(binding.imageView)

        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        val fadeIN = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        binding.imageView.animation = fadeIN
        binding.textView.animation = bottomAnim
        binding.textView2.animation = topAnim

        val handler = Handler(Looper.getMainLooper())

        val delayedRunnable = Runnable {
            if (!PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                viewModel.getAppDetails("User", StaticValue.REST_ID.toInt())
            else {
                startActivity(Intent(applicationContext, MainActivity2::class.java))
                finish()
            }

        }

        handler.postDelayed(delayedRunnable, 2200) // 5000 milliseconds = 5 seconds

    }

    override fun onStarted() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onFailure(message: String, type: String) {
        log("SplashActivity", " onFailure $message")
        binding.progressBar.visibility = View.GONE
        when (type) {
            CommonKey.ERROR_CODE_401 -> {
                dialogSessionExpire(
                    this,
                )
            }
            else -> showAlert(
                this,
                getString(R.string.alert),
                message
            )
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progressBar.visibility = View.GONE

        log("SplashActivity", " onSuccess $dataG")

        val data: CompanyData = dataG as CompanyData

        PreferenceProvider(applicationContext).setStringValue(
            data.compony_name!!, PreferenceKey.COMPANY_NAME
        )
        log("SplashActivity ", " ${data.Token}")
        PreferenceProvider(applicationContext).setStringValue(
            data.Token!!, PreferenceKey.APP_TOKEN
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.compony_image!!, PreferenceKey.IMAGE_LOGO
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.currency!!, PreferenceKey.CURRENCY_TYPE
        )

        PreferenceProvider(applicationContext).setStringValue(
            data.currency_symbol!!, PreferenceKey.CURRENCY_SYMBOL
        )
        startActivity(Intent(applicationContext, MainActivity2::class.java))
        finish()

    }


}