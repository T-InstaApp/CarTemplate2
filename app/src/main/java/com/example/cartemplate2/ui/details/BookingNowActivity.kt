package com.example.cartemplate2.ui.details


import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cartemplate2.databinding.ActivityBookNowBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.utils.*
import java.util.*


class BookingNowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookNowBinding
    private lateinit var toolbar: ToolbarBinding

    private var productID = 0
    private var productName = ""
    private var productPrice = ""
    private var brandName = ""
    private var discount = 0
    private var discountType = "Percentage"
    private var carType = ""
    private var locationType = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookNowBinding.inflate(layoutInflater)
        setContentView(binding.root)


        productID = intent.getStringExtra("id")!!.toInt()
        productName = intent.getStringExtra("name")!!
        productPrice = intent.getStringExtra("price")!!
        brandName = intent.getStringExtra("brand")!!

        discountType = intent.getStringExtra("discount_type")!!
        discount = intent.getStringExtra("discount")!!.toInt()

        binding.txtBrand.text = "- $brandName"
        binding.txtModel.text = " - $productName"

        if (discountType == "Percentage") {
            val discountAmount = ((productPrice.toDouble() * discount) / 100).toInt()
            productPrice = (productPrice.toDouble() - discountAmount).toString()
        } else if (discountType == "Amount") {
            productPrice = (productPrice.toInt() - discount).toString()
        }


        binding.txtPrice.text =
            " - ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(productPrice.toDouble().toLong())
            }"

        //Calculate booking amount
        val bookingAmount = (productPrice.toDouble() * 25) / 100
        binding.txtBookingAmount.text =
            " - ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(bookingAmount.toLong())
            }"
        try {
            binding.txtKey1.text = intent.getStringExtra("key1")
            binding.txtValue1.text = " - ${intent.getStringExtra("value1")}"
            binding.txtKey2.text = intent.getStringExtra("key2")
            binding.txtValue2.text = " - ${intent.getStringExtra("value2")}"
            binding.txtKey3.text = intent.getStringExtra("key3")
            binding.txtValue3.text = " - ${intent.getStringExtra("value3")}"
            binding.txtKey4.text = intent.getStringExtra("key4")
            binding.txtValue4.text = " - ${intent.getStringExtra("value4")}"
        } catch (ignore: Exception) {
        }
        binding.bookNow.setOnClickListener { }

    }
}