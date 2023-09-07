package com.example.cartemplate2.ui.details


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.*
import com.example.cartemplate2.datamodel.CarSpecificationDataModel
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.model.CarViewModel
import com.example.cartemplate2.datamodel.ProductImagesDataModel
import com.example.cartemplate2.datamodel.Specification
import com.example.cartemplate2.provider.CarViewModelFactory
import com.example.cartemplate2.ui.adapter.BannerViewPagerAdapter
import com.example.cartemplate2.ui.adapter.CarSpecificationDataAdapter
import com.example.cartemplate2.ui.adapter.ImageGroupNameDataAdapter
import com.example.cartemplate2.ui.auth.LoginActivity
import com.example.cartemplate2.utils.*
import com.google.gson.JsonObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class DetailsActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {

    override val kodein by kodein()
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: CarViewModel
    private val factory: CarViewModelFactory by instance()
    lateinit var bannerViewPagerAdapter: BannerViewPagerAdapter
    lateinit var progressLayout: ProgressDialogLayoutBinding
    lateinit var specificationAdapter: CarSpecificationDataAdapter
    lateinit var inspectionAdapter: CarSpecificationDataAdapter
    var specificationCount = 6
    var inspectionCount = 6
    private var productID = 0
    private var productName = ""
    private var productPrice = ""
    var brand = ""
    val discountType = "Percentage"
    val discount = 15

    // Declare variables for the timer and timer task
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private val delay: Long = 5000 // Change this value as needed

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressLayout = binding.progressBar

        viewModel = ViewModelProvider(this, factory)[CarViewModel::class.java]
        viewModel.networkCallListener = this
        toolbar = binding.toolBar
        toolbar.txtTopHeading.text = "Details Activity"
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        productID = intent.getStringExtra("id")!!.toInt()
        productName = intent.getStringExtra("name")!!
        productPrice = intent.getStringExtra("price")!!
        brand = intent.getStringExtra("brand")!!

        binding.productName.text = productName
        binding.txtSpecificationHeading.text = "Key Specification for $productName"
        binding.txtInspectionHeading.text = "Inspection Key Report for $productName"

        formatCurrency(binding.productPrice, productPrice)

        binding.btnTestDrive.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS)) {
                val intent = Intent(applicationContext, TestDriveBookingActivity::class.java)
                intent.putExtra("name", productName)
                intent.putExtra("id", productID.toString())
                intent.putExtra("price", productPrice)
                intent.putExtra("brand", brand)
                startActivity(intent)
            } else {
                showLoginDialog("Ready to Book Your Test Drive? Let's Get Started – Please Log In!")
            }
        }

        binding.BookNow.setOnClickListener {
            if (PreferenceProvider(applicationContext).getBooleanValue(PreferenceKey.LOGIN_STATUS))
                openBookingDialog()
            else
                showLoginDialog("Ready to Book Your Car? Let's Get Started – Please Log In!")
        }

        getCarImage()
        getCarSpecification()



        binding.txtViewAllSpecification.setOnClickListener {
            if (binding.txtViewAllSpecification.text.contains("Show")) {
                specificationAdapter.updateSize(specificationCount)
                binding.txtViewAllSpecification.text =
                    Html.fromHtml("<u>Hide All Specification</u>", Html.FROM_HTML_MODE_LEGACY)
            } else {
                specificationAdapter.updateSize(6)
                binding.txtViewAllSpecification.text =
                    Html.fromHtml("<u>Show All Specification</u>", Html.FROM_HTML_MODE_LEGACY)
            }
        }
        binding.txtViewAllInspection.setOnClickListener {
            if (binding.txtViewAllInspection.text.contains("Show")) {
                inspectionAdapter.updateSize(inspectionCount)
                binding.txtViewAllInspection.text =
                    Html.fromHtml("<u>Hide All Inspections</u>", Html.FROM_HTML_MODE_LEGACY)
            } else {
                inspectionAdapter.updateSize(6)
                binding.txtViewAllInspection.text =
                    Html.fromHtml("<u>Show All Inspections</u>", Html.FROM_HTML_MODE_LEGACY)
            }
        }
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
        } else if (type == "getProductSpecificationId") {
            val data: CarSpecificationDataModel = dataG as CarSpecificationDataModel
            loadRecyclerView(data)
        } else if (type == "bookCar") {
            toast("your car booking request is successful submitted")
        }
    }

    private fun getCarImage() {
        viewModel.getProductImages(
            "Token fa0085519cb9f216875d2291cd383fe8483bc0b4", productID
        )
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
            val imageAdapter = ImageGroupNameDataAdapter(groupDataList, this, applicationContext)
            binding.imageGroupRecyclerView.adapter = imageAdapter
            showAllGroupImages(groupData[0].group_name)
        }
    }

    private fun showAllGroupImages(gName: String) {
        // Load Default Image
        val imageList: ArrayList<String> = java.util.ArrayList()
        for (d in groupData) {
            if (d.group_name == gName) {
                for (data in d.images) {
                    imageList.add(data.image_url!!)
                }
                bannerViewPagerAdapter =
                    BannerViewPagerAdapter(this, imageList, "Details", productID, productName)
                binding.bannerKocMarket.adapter = bannerViewPagerAdapter
                binding.tabDots.setupWithViewPager(binding.bannerKocMarket, true)
                startAutoSlide()
            }
        }
    }

    private fun groupImagesByGroupName(images: List<ProductImagesDataModel>): List<ImageGroupedResponse> {
        val groupedMap = images.groupBy { it.group_name }
        return groupedMap.map { (groupName, imageList) ->
            ImageGroupedResponse(groupName!!, imageList)
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
        if (status == "Inspection") {
            val intent = Intent(applicationContext, InspectionReportActivity::class.java)
            intent.putExtra("issue_name", dataG.toString().split("#")[0])
            intent.putExtra("extra", dataG.toString().split("#")[1])
            intent.putExtra("image", dataG.toString().split("#")[2])
            log("onRecyclerItemClick", "==$dataG ${dataG.toString().split("#")[2]}")
            startActivity(intent)
        } else
            showAllGroupImages(dataG.toString())
    }

    private fun getCarSpecification() {
        viewModel.getProductSpecificationId(
            "Token ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)}",
            productID
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadRecyclerView(carSpecificationDataModel: CarSpecificationDataModel) {
        val groupedSpecifications =
            groupSpecificationsByType(carSpecificationDataModel.specification)

        val tempSpecificationData: ArrayList<TempSpecificationData> = arrayListOf()
        for ((type, typeList) in groupedSpecifications) {
            tempSpecificationData.add(TempSpecificationData(type, typeList))
        }
        for (d in tempSpecificationData) {
            if (d.type.equals("Specification")) {
                val layoutManager = GridLayoutManager(applicationContext, 2)
                binding.SpecificationRecyclerView.layoutManager = layoutManager
                specificationAdapter = CarSpecificationDataAdapter(
                    d.specification, "Specification", this, specificationCount
                )
                binding.SpecificationRecyclerView.adapter = specificationAdapter

                specificationCount = d.specification.size
            } else if (d.type.equals("Inspection")) {
                val layoutManager = GridLayoutManager(applicationContext, 2)
                binding.InspectionRecyclerView.layoutManager = layoutManager
                inspectionAdapter = CarSpecificationDataAdapter(
                    d.specification, "Inspection", this, inspectionCount
                )
                binding.InspectionRecyclerView.adapter = inspectionAdapter
                inspectionCount = d.specification.size
            }
        }
    }

    private fun groupSpecificationsByType(specifications: List<Specification>): Map<String, ArrayList<Specification>> {
        val groupedMap = mutableMapOf<String, ArrayList<Specification>>()

        for (specification in specifications) {
            val type = specification.type
            if (type != null) {
                val typeList = groupedMap.getOrPut(type) { ArrayList() }
                typeList.add(specification)
            }
        }

        return groupedMap
    }

    @SuppressLint("SetTextI18n")
    private fun openBookingDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_book_now)
        dialog.setCancelable(false)

        val bookNow = dialog.findViewById<AppCompatButton>(R.id.bookNow)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btnCancel)

        val txtBrand = dialog.findViewById<TextView>(R.id.txtBrand)
        val txtModel = dialog.findViewById<TextView>(R.id.txtModel)
        val txtPrice = dialog.findViewById<TextView>(R.id.txtPrice)
        val txtBookingAmount = dialog.findViewById<TextView>(R.id.txtBookingAmount)

        txtBrand.text = " - $brand"
        txtModel.text = " - $productName"

        if (discountType == "Percentage") {
            val discountAmount = ((productPrice.toDouble() * discount) / 100).toInt()
            productPrice = (productPrice.toDouble() - discountAmount).toString()
        } else if (discountType == "Amount") {
            productPrice = (productPrice.toInt() - discount).toString()
        }

        txtPrice.text =
            " - ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(productPrice.toDouble().toLong())
            }"

        //Calculate booking amount
        val bookingAmount = (productPrice.toDouble() * 25) / 100
        txtBookingAmount.text =
            " - ${PreferenceProvider(applicationContext).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(bookingAmount.toLong())
            }"

        btnCancel.setOnClickListener { dialog.dismiss() }

        bookNow.setOnClickListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("subtotal", "0.00")
            jsonObject.addProperty("total", bookingAmount.toString())
            jsonObject.addProperty("discount", "0.0")
            jsonObject.addProperty("extra", "NA")
            jsonObject.addProperty("tax", "0.0")
            jsonObject.addProperty("service_fee", "0.0")
            jsonObject.addProperty("status", "active")
            jsonObject.addProperty(
                "customer",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.USER_ID)
            )
            jsonObject.addProperty("restaurant_id", StaticValue.REST_ID)
            jsonObject.addProperty("product_id", productID)
            jsonObject.addProperty("receipt_email", "")
            jsonObject.addProperty(
                "currency",
                PreferenceProvider(applicationContext).getIntValue(PreferenceKey.CURRENCY_TYPE)
            )
            jsonObject.addProperty("type", "cash")
            jsonObject.addProperty("coupon", "")
            jsonObject.addProperty("transaction_id", "1234")
            jsonObject.addProperty("transaction_id", "1234")
            jsonObject.addProperty("source", "Mobile")

            log("DetailsActivity", "data $jsonObject")

            viewModel.bookCar(
                "Token ${
                    PreferenceProvider(applicationContext).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )
                }", jsonObject
            )

            dialog.dismiss()

        }


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = (getScreenWidthSize(this) * 0.9).toInt()
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showLoginDialog(msg: String) {
        val dialog = createAlertDialogObject(this)
        val btnOk = dialog.findViewById<AppCompatButton>(R.id.btnAdd)
        val txtHeading = dialog.findViewById<TextView>(R.id.txtHeading)
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        txtMessage.text = msg
        txtHeading.text = getString(R.string.alert)
        btnOk.text = getString(R.string.login)
        btnOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.putExtra("callFrom", "Details")
            startActivity(intent)
        }
        dialog.show()
    }
}

data class ImageGroupedResponse(
    val group_name: String, val images: List<ProductImagesDataModel>
)

data class ImageUrlWithGroupName(
    val gName: String, val url: String, var isSelected: Boolean = false
)

data class TempSpecificationData(
    val type: String?, val specification: ArrayList<Specification>
)

