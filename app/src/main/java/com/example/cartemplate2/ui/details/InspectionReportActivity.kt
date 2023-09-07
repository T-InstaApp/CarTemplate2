package com.example.cartemplate2.ui.details

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityInspectionReportBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.ui.adapter.BannerViewPagerAdapter
import java.util.*
import kotlin.collections.ArrayList

class InspectionReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInspectionReportBinding
    private lateinit var toolbar: ToolbarBinding
    private var name = ""
    private var extra = ""
    private var url = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspectionReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        name = intent.getStringExtra("issue_name")!!
        extra = intent.getStringExtra("extra")!!
        url = intent.getStringExtra("image")!!

        toolbar = binding.toolBar
        toolbar.txtTopHeading.text = name
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        binding.txtIssueDetails.text = "Details - $extra"

        val imageList: ArrayList<String> = ArrayList(url.split("@"))

        val bannerViewPagerAdapter =
            BannerViewPagerAdapter(this, imageList, "InspectionReport", 0, "")
        binding.bannerKocMarket.adapter = bannerViewPagerAdapter
        binding.tabDots.setupWithViewPager(binding.bannerKocMarket, true)

    }
}