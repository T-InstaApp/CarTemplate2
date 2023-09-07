package com.example.cartemplate2.ui.details


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityTestDriveBookingBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.model.CarViewModel
import com.example.cartemplate2.provider.CarViewModelFactory
import com.example.cartemplate2.utils.*
import com.google.gson.JsonObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TestDriveBookingActivity : AppCompatActivity(), KodeinAware, NetworkCallListener {

    private lateinit var binding: ActivityTestDriveBookingBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var progressBinding: ProgressDialogLayoutBinding
    override val kodein by kodein()
    private lateinit var viewModel: CarViewModel
    private val factory: CarViewModelFactory by instance()

    private var productID = 0
    private var productName = ""
    private var productPrice = ""
    private var brandName = ""
    private var carType = ""
    private var locationType = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestDriveBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[CarViewModel::class.java]
        viewModel.networkCallListener = this

        toolbar = binding.toolBar
        toolbar.txtTopHeading.text = "Book Your Test Drive"
        progressBinding = binding.progress
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }


        productID = intent.getStringExtra("id")!!.toInt()
        productName = intent.getStringExtra("name")!!
        productPrice = intent.getStringExtra("price")!!
        brandName = intent.getStringExtra("brand")!!


        formatCurrency(binding.productPrice, productPrice)
        binding.productName.text = "$brandName $carType $productName"
        binding.etDate.setOnClickListener { showRoundedDatePickerDialog() }

        binding.etTime.setOnClickListener { showTimeIntervalDialog() }

        binding.radioGLocationType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioHome -> {
                    locationType = findViewById<RadioButton>(checkedId).text.toString()
                    binding.addressLayout.visibility = View.VISIBLE
                }
                R.id.radioCompany -> {
                    locationType = findViewById<RadioButton>(checkedId).text.toString()
                    binding.addressLayout.visibility = View.GONE
                }
            }
        }

        binding.btnBookTestDrive.setOnClickListener { checkValidation() }

    }

    private fun showRoundedDatePickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_rounded_date_picker)
        val datePicker: DatePicker = dialog.findViewById(com.example.cartemplate2.R.id.datePicker)
        val submitButton: Button = dialog.findViewById(com.example.cartemplate2.R.id.submitButton)
        submitButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year

            binding.etDate.setText("$year-$month-$day")
            dialog.dismiss()
        }
        dialog.show()
    }

    lateinit var alertDialog: AlertDialog
    private fun showTimeIntervalDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Time Interval")
        val timeIntervals = generateTimeIntervals(1, 24)

        val adapter = TimeIntervalAdapter(this, timeIntervals)
        builder.setAdapter(adapter, null)

        builder.setNegativeButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun generateTimeIntervals(startHour: Int, endHour: Int): Array<String> {
        val intervalsList = ArrayList<String>()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        for (hour in startHour until endHour step 2) {
            val startTime1 = Calendar.getInstance()
            startTime1.set(Calendar.HOUR_OF_DAY, hour)
            startTime1.set(Calendar.MINUTE, 0)

            val endTime1 = Calendar.getInstance()
            endTime1.set(Calendar.HOUR_OF_DAY, hour + 1)
            endTime1.set(Calendar.MINUTE, 0)

            val interval1 = "${sdf.format(startTime1.time)} - ${sdf.format(endTime1.time)}"

            val startTime2 = Calendar.getInstance()
            startTime2.set(Calendar.HOUR_OF_DAY, hour + 1)
            startTime2.set(Calendar.MINUTE, 0)

            val endTime2 = Calendar.getInstance()
            endTime2.set(Calendar.HOUR_OF_DAY, hour + 2)
            endTime2.set(Calendar.MINUTE, 0)

            val interval2 = "${sdf.format(startTime2.time)} - ${sdf.format(endTime2.time)}"

            intervalsList.add("$interval1, $interval2")
        }

        return intervalsList.toTypedArray()
    }

    private inner class TimeIntervalAdapter(
        context: AppCompatActivity,
        private val values: Array<String>,
    ) : ArrayAdapter<String>(context, R.layout.list_item_time_interval, values) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.list_item_time_interval, parent, false)

            val textViewInterval1 = view.findViewById<TextView>(R.id.textViewInterval1)
            val textViewInterval2 = view.findViewById<TextView>(R.id.textViewInterval2)

            val intervalPair = values[position].split(", ")
            textViewInterval1.text = intervalPair[0]
            textViewInterval2.text = intervalPair[1]
            textViewInterval1.setOnClickListener {
                binding.etTime.setText(textViewInterval1.text.toString())
                alertDialog.dismiss()
            }
            textViewInterval2.setOnClickListener {
                binding.etTime.setText(textViewInterval2.text.toString())
                alertDialog.dismiss()
            }

            return view
        }
    }

    private fun checkValidation() {
        if (locationType.length < 3) {
            toast("Please select location type")
            return
        } else if (locationType.equals("Home", ignoreCase = true)) {
            if (binding.etFullName.text.toString().length < 2) {
                toast("Please enter your full name")
                return
            } else if (binding.etPhone.text.toString().length < 2) {
                toast("Please enter valid phone no")
                return
            } else if (binding.etLocality.text.toString().length < 8) {
                toast("Please enter address")
                return
            } else if (binding.etPostalCode.text.toString().length < 5) {
                toast("Please enter postal code")
                return
            } else if (binding.etCity.text.toString().isEmpty()) {
                toast("Please enter city")
                return
            } else if (binding.etState.text.toString().isEmpty()) {
                toast("Please enter state")
                return
            } else if (binding.etLandMark.text.toString().length < 5) {
                toast("Please enter landmark")
                return
            } else if (binding.etDate.text.toString().length < 5) {
                toast("Please select date")
                return
            } else if (binding.etTime.text.toString().length < 5) {
                toast("Please select time")
                return
            }
        } else if (binding.etDate.text.toString().length < 5) {
            toast("Please select date")
            return
        } else if (binding.etTime.text.toString().length < 5) {
            toast("Please select time")
            return
        }
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            "customer",
            PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
        )
        jsonObject.addProperty("product", productID)
        jsonObject.addProperty("company", StaticValue.REST_ID)
        jsonObject.addProperty("user_comment", "")
        jsonObject.addProperty("door_step", false)
        jsonObject.addProperty("complete_address", "")
        jsonObject.addProperty("status", true)
        jsonObject.addProperty(
            "requested_date_time",
            "${binding.etDate.text} ${binding.etTime.text}"
        )//

        if (locationType.equals("Home", ignoreCase = true)) {
            val address =
                "${binding.etFullName.text} \n${binding.etLocality.text}, ${binding.etCity.text}, ${binding.etState.text} - ${binding.etPostalCode.text}, near ${binding.etLandMark.text} (${binding.etPhone.text})"
            jsonObject.addProperty("door_step", true)
            jsonObject.addProperty("complete_address", address)
        }

        viewModel.bookTestDrive(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", jsonObject
        )

    }

    override fun onStarted() {
        progressBinding.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressBinding.progressLayout.notVisible()
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
        progressBinding.progressLayout.notVisible()
        val dialog = createAlertDialogObject(this)
        val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnAdd)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = getString(R.string.test_drive_booking_success)
        txtHeading.text = getString(R.string.success)
        btnOk.text = getString(R.string.ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}