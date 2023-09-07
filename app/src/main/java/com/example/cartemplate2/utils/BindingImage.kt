package com.example.cartemplate2.utils

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.cartemplate2.R
import java.math.BigDecimal
import java.text.*
import java.util.*
import kotlin.Boolean
import kotlin.String

@BindingAdapter("image")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view).load(url).circleCrop().into(view)
}

@BindingAdapter("imageSquare")
fun loadImageSquare(view: ImageView, url: String?) {
    if (url != null && url.length > 10)
        Glide.with(view).load(url).into(view)
}

@BindingAdapter("imageRound")
fun loadRoundImage(view: RoundedImageView, url: String) {
    if (url.length > 5) {
        Glide.with(view).load(url).into(view)
    }

}

@BindingAdapter("dateFormat2")
fun formatDate2(view: TextView, date: String) {
    val old = "yyyy-MM-dd"
    val newFormat = "dd MMMM yyyy"
    var changeDate: String? = null
    try {
        val format: DateFormat = SimpleDateFormat(old, Locale.ENGLISH)
        val date = format.parse(date)
        val newformat = SimpleDateFormat(newFormat, Locale.ENGLISH)
        val newDate = newformat.format(date!!)
        changeDate =
            newDate.split(" ".toRegex())
                .toTypedArray()[1] + " " + newDate.split(" ".toRegex())
                .toTypedArray()[0] + ", " + newDate.split(" ".toRegex()).toTypedArray()[2]
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    view.text = changeDate
}

@BindingAdapter("dateFormat")
fun formatDate(view: TextView, dateTime: String) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    val parsedDate = inputFormat.parse(dateTime)
    view.text = outputFormat.format(parsedDate)
}


@BindingAdapter("TextTitle")
fun setTextTitle(view: TextView, title: String) {
    view.text = title
}

@BindingAdapter("SetEditTextValue")
fun editTextValue(view: EditText, title: String) {
    view.setText(title)
}


@BindingAdapter("enable")
fun setEnable(view: EditText, data: Boolean?) {
    if (data != null) {
        view.isEnabled = data
    }
}


@SuppressLint("SetTextI18n")
@BindingAdapter("FormatCurrency")
fun formatCurrency(view: TextView, price: String) {
    try {
        val cleanString = price.replace(",", "")
        val parsed = BigDecimal(cleanString)
        val formatter = DecimalFormat("#,###.##", DecimalFormatSymbols(Locale.ENGLISH))
        val formattedText =
            "${PreferenceProvider(view.context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatter.format(parsed)
            }"
        view.text = formattedText
    } catch (ignore: Exception) {
    }
}

fun getImageID(type: String): Int {
    return when {
        type.contains("ABS", ignoreCase = true) -> R.drawable.ic_abs_light
        type.contains("Locking", ignoreCase = true) -> R.drawable.airbag
        type.contains("Air Bags", ignoreCase = true) -> R.drawable.airbag
        type.contains("Fuel Type", ignoreCase = true) -> R.drawable.ic_fuel
        type.contains("Insurance Type", ignoreCase = true) -> R.drawable.ic_nsurance
        type.contains("Date", ignoreCase = true) -> R.drawable.ic_date
        type.contains("Number", ignoreCase = true) -> R.drawable.ic_licence_plate
        type.contains("Engine Capacity", ignoreCase = true) -> R.drawable.ic_engine
        type.contains("Validity", ignoreCase = true) -> R.drawable.ic_date
        type.contains("KM", ignoreCase = true) -> R.drawable.ic_km
        type.contains("Transmission", ignoreCase = true) -> R.drawable.ic_transmission
        type.contains("Ownership", ignoreCase = true) -> R.drawable.ic_ownershipicon
        type.contains("Steering", ignoreCase = true) -> R.drawable.ic_steering_wheel
        type.contains("Connectivity", ignoreCase = true) -> R.drawable.airbag
        type.contains("Cruise", ignoreCase = true) -> R.drawable.ic_cruise_control2
        type.contains("Sensors", ignoreCase = true) -> R.drawable.ic_sensor
        type.contains("Wheels", ignoreCase = true) -> R.drawable.ic_wheel
        type.contains("sunroof", ignoreCase = true) -> R.drawable.ic_sunroof
        type.contains("lamps", ignoreCase = true) -> R.drawable.ic_headlight
        type.contains("Brake", ignoreCase = true) -> R.drawable.ic_brakes
        type.contains("Cylinders", ignoreCase = true) -> R.drawable.ic_cylinder
        type.contains("Displacement", ignoreCase = true) -> R.drawable.ic_engine_power
        type.contains("Body", ignoreCase = true) -> R.drawable.ic_car_body
        type.contains("Sitting", ignoreCase = true) -> R.drawable.ic_seat2
        type.contains("Tank", ignoreCase = true) -> R.drawable.ic_tank
        type.contains("Power", ignoreCase = true) -> R.drawable.ic_engine_power
        else -> R.drawable.ic_km
    }
}


fun formatNumberToWord(number: Long): String {
    return when {
        number >= 10000000 -> {
            val crore = number / 10000000
            val remainder = number % 10000000
            val formattedRemainder = String.format("%02d", remainder / 100000)
            "$crore.$formattedRemainder Crore"
        }
        number >= 100000 -> {
            val lakh = number / 100000
            val remainder = number % 100000
            val formattedRemainder = String.format("%02d", remainder / 1000)
            "$lakh.$formattedRemainder Lakh"
        }
        else -> {
            val thousand = number / 1000
            val remainder = number % 1000
            val formattedRemainder = String.format("%02d", remainder / 10)
            "$thousand.$formattedRemainder K"
        }
    }
}
