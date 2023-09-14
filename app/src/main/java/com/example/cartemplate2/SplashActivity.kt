package com.example.cartemplate2


import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.cartemplate2.databinding.ActivitySplashBinding
import com.example.cartemplate2.datamodel.CompanyData
import com.example.cartemplate2.datamodel.TemplateData
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.HomeViewModel
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.utils.*
import com.google.gson.JsonObject
import okhttp3.FormBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.lang.reflect.Type

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
            if (!PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                viewModel.getAppDetails("User", StaticValue.REST_ID.toInt())
                // updateTrailTime()
            } else {
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

    fun updateTrailTime() {
        log("getUpdateTrailTime  ", " Called ")
        val jsonObject = JsonObject()
        val data = getAppVersion(applicationContext)

        toast("Version :- ${data!!.versionName}")

        jsonObject.addProperty("template_id", "1")
        jsonObject.addProperty("build_version", "1.0.3")//data!!.versionNumber
        jsonObject.addProperty("type", "Android")
        jsonObject.addProperty("start_time", System.currentTimeMillis().toString())
        val timestampPlus24HoursMs = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
        jsonObject.addProperty("end_time", timestampPlus24HoursMs)


        /*  val data = getAppVersion(applicationContext)
          val timestampPlus24HoursMs = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
          val formBody = FormBody.Builder()
              .add("template_id", "value1")
              .add("build_version", data!!.versionNumber.toString())
              .add("type", "Android")
              .add("start_time", System.currentTimeMillis().toString())
              .add("end_time", timestampPlus24HoursMs.toString())
              .build()*/

        viewModel.getUpdateTrailTime(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", jsonObject
        )
    }


    private fun getAppVersion(
        context: Context,
    ): AppVersion? {
        return try {
            val packageManager = context.packageManager
            val packageName = context.packageName
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
            AppVersion(
                versionName = packageInfo.versionName,
                versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo),
            )
        } catch (e: Exception) {
            toast("Version :- $e")
            null
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        binding.progressBar.visibility = View.GONE
        if (type == "getAppDetails") {
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
            updateTrailTime()
        } else if (type == "updateTrailTime") {
            val data: TemplateData = dataG as TemplateData
            val endTime = data.end_time!!.toLong()
            val currentTime = System.currentTimeMillis()
            if (endTime < currentTime) {
                showAlert(
                    this,
                    getString(R.string.alert),
                    "Sorry, your trial has expired. Get in touch with us to continue using the app."
                )
            } else {
                startActivity(Intent(applicationContext, MainActivity2::class.java))
                finish()
            }
        }
    }
}

data class AppVersion(
    val versionName: String,
    val versionNumber: Long,
)