package com.example.cartemplate2.ui.details

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityZoomImageBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.ProductImagesDataModel
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.model.CarViewModel
import com.example.cartemplate2.provider.CarViewModelFactory
import com.example.cartemplate2.ui.adapter.BannerViewPagerAdapter
import com.example.cartemplate2.ui.adapter.ImageGroupNameDataAdapter2
import com.example.cartemplate2.utils.*
import com.google.vr.sdk.widgets.pano.VrPanoramaView
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class ZoomImageActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {

    private lateinit var binding: ActivityZoomImageBinding
    override val kodein by kodein()
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: CarViewModel
    private val factory: CarViewModelFactory by instance()
    lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    lateinit var progressLayout: ProgressDialogLayoutBinding

    // Declare variables for the timer and timer task
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private val delay: Long = 5000
    private var productID = 0
    private var productName = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityZoomImageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this, factory)[CarViewModel::class.java]
        viewModel.networkCallListener = this
        toolbar = binding.toolBar
        toolbar.txtTopHeading.text = "View Image"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }
        progressLayout = binding.progressBar
        productID = intent.getStringExtra("id")!!.toInt()
        productName = intent.getStringExtra("name")!!

        binding.photoView.setImageResource(R.drawable.car_hd2)
        loadPanoramaImage()
        getCarImage()
    }

    private fun loadPanoramaImage() {
        val options = VrPanoramaView.Options()
        try {
            options.inputType = VrPanoramaView.Options.TYPE_MONO
            binding.viewPanaroma.loadImageFromBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.panorama
                ), options
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCarImage() {
        viewModel.getProductImages(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productID
        )
    }

    override fun onStarted() {
        progressLayout.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressLayout.progressLayout.notVisible()
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
        progressLayout.progressLayout.notVisible()
        if (type == "getProductImages") {
            val data: ArrayList<ProductImagesDataModel> = dataG as ArrayList<ProductImagesDataModel>
            showProductImage(data)
        }
    }

    var groupData: List<ImageGroupedResponse> = emptyList()
    private fun showProductImage(imageData: ArrayList<ProductImagesDataModel>) {
        groupData = groupImagesByGroupName(imageData)
        val groupDataList: ArrayList<ImageUrlWithGroupName> = arrayListOf()
        for (gData in groupData) {
            groupDataList.add(ImageUrlWithGroupName(gData.group_name, gData.images[0].image_url!!))
        }
        if (groupDataList.size > 0) {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.imageGroupRecyclerView.layoutManager = layoutManager
            val imageAdapter = ImageGroupNameDataAdapter2(groupDataList, this, applicationContext)
            binding.imageGroupRecyclerView.adapter = imageAdapter
            showAllGroupImages(groupData[0].group_name)
        }
    }

    private fun groupImagesByGroupName(images: List<ProductImagesDataModel>): List<ImageGroupedResponse> {
        val groupedMap = images.groupBy { it.group_name }
        return groupedMap.map { (groupName, imageList) ->
            ImageGroupedResponse(groupName!!, imageList)
        }
    }

    private fun showAllGroupImages(gName: String) {
        // Load Default Image
        val imageList: ArrayList<String> = ArrayList()
        for (d in groupData) {
            if (d.group_name == gName) {
                for (data in d.images) {
                    imageList.add(data.image_url!!)
                }
                bannerViewPagerAdapter =
                    BannerViewPagerAdapter(this, imageList, "Zoom", productID, productName)
                binding.bannerKocMarket.adapter = bannerViewPagerAdapter
                binding.tabDots.setupWithViewPager(binding.bannerKocMarket, true)

            }
        }
    }

    // Start the automatic slide changes
    private fun startAutoSlide() {
        // Cancel any existing timer and task
        stopAutoSlide()
        // Create a new timer and task
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                // Perform slide change logic here
                runOnUiThread {
                    val currentItem = binding.bannerKocMarket.currentItem
                    val nextItem = (currentItem + 1) % binding.bannerKocMarket.adapter?.count!!
                    binding.bannerKocMarket.setCurrentItem(nextItem, true)
                }
            }
        }
        // Schedule the task with the desired delay
        timer?.schedule(timerTask, delay, delay)
    }

    // Stop the automatic slide changes
    private fun stopAutoSlide() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        showAllGroupImages(dataG.toString())
    }


}