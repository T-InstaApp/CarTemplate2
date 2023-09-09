package com.example.cartemplate2.ui.details

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityProductListBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.CategoryResponse
import com.example.cartemplate2.datamodel.OverviewsLabelData
import com.example.cartemplate2.datamodel.ProductDataMode
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.model.HomeViewModel
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.ui.adapter.ProductListDataAdapter
import com.example.cartemplate2.utils.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ProductListActivity : AppCompatActivity(), KodeinAware, NetworkCallListener,
    RecyclerViewClickListener {
    override val kodein by kodein()
    private lateinit var binding: ActivityProductListBinding
    private lateinit var toolbar: ToolbarBinding
    private lateinit var viewModel: HomeViewModel
    lateinit var progressLayout: ProgressDialogLayoutBinding
    private val factory: HomeViewModelFactory by instance()
    var categoryId = ""
    var categoryName = ""
    private lateinit var filterLabelData: ArrayList<OverviewsLabelData>
    lateinit var catData: ArrayList<CategoryResponse>
    private lateinit var chipBorderDrawable: Drawable
    private val selectedValue = HashMap<String, String>()
    private var type = "Other"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        progressLayout = binding.progressBar
        toolbar = binding.toolBar
        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { finish() }

        categoryId = intent.getStringExtra("id")!!.toString()
        categoryName = intent.getStringExtra("name")!!
        type = intent.getStringExtra("type")!!

        toolbar.txtTopHeading.text = categoryName
        chipBorderDrawable = ContextCompat.getDrawable(this, R.drawable.border_line)!!

        if (type == "Search")
            loadSearchData()
        else if (type == "All Category")
            loadALLData()
        else
            loadCategoryData()

        filterLabelData = arrayListOf()
        catData = arrayListOf()
        viewModel.getOverviewLabelData(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", "Resale"
        )

        viewModel.getCategory(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", StaticValue.REST_ID
        )


        binding.fabFilter.setOnClickListener { openFilterDialog() }
    }

    private fun loadSearchData() {
        viewModel.searchProduct(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            categoryName
        )
    }

    private fun loadCategoryData() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "Category",
            categoryId
        )
    }

    private fun loadALLData() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(applicationContext).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "All",
            ""
        )
    }

    override fun onStarted() {
        progressLayout.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        log("appliedFilter", " onFailure == $message $type")
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
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("SetTextI18n")
    override fun <T> onSuccess(dataG: T, type: String) {
        progressLayout.progressLayout.notVisible()
        when (type) {
            "Category", "searchProduct", "All" -> {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                binding.productList.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                binding.productList.setHasFixedSize(true)
                binding.productList.adapter = ProductListDataAdapter(data, this, applicationContext)

                toolbar.txtTopHeading.text = categoryName + " (${data.size})"
            }
            "getOverviewLabelData" -> {
                filterLabelData = dataG as ArrayList<OverviewsLabelData>
            }
            "getCategoryListData" -> {
                catData = dataG as ArrayList<CategoryResponse>
            }
            "getProductFilterData" -> {
                toolbar.txtTopHeading.text = "Used Cars"
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                binding.productList.layoutManager =
                    LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                binding.productList.setHasFixedSize(true)
                binding.productList.adapter = ProductListDataAdapter(data, this, applicationContext)
            }
        }
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        if (status == "Details") {
            val intent = Intent(applicationContext, DetailsActivity::class.java)
            intent.putExtra("name", dataG.toString().split("@")[0])
            intent.putExtra("id", dataG.toString().split("@")[1])
            intent.putExtra("price", dataG.toString().split("@")[2])
            intent.putExtra("brand", dataG.toString().split("@")[3])
            startActivity(intent)
        }
    }

    lateinit var dialog: Dialog
    var isOpenFirst = false


    @SuppressLint("SetTextI18n", "InflateParams")
    private fun openFilterDialog() {
        if (!isOpenFirst) {
            dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.filter_screen)
            dialog.setCancelable(true)
            isOpenFirst = true
            val selectedShipGroup = dialog.findViewById<ChipGroup>(R.id.selectedShipGroup)
            val brandChipGroup = dialog.findViewById<ChipGroup>(R.id.brandChipGroup)
            val modelYearChipGroup = dialog.findViewById<ChipGroup>(R.id.modelYearChipGroup)
            val bodyTypeChipGroup = dialog.findViewById<ChipGroup>(R.id.bodyTypeChipGroup)
            val priceRangeChipGroup = dialog.findViewById<ChipGroup>(R.id.priceRangeChipGroup)
            val fuelChipGroup = dialog.findViewById<ChipGroup>(R.id.fuelChipGroup)
            val transmissionChipGroup = dialog.findViewById<ChipGroup>(R.id.transmissionChipGroup)
            val ownerChipGroup = dialog.findViewById<ChipGroup>(R.id.ownerChipGroup)
            val kmChipGroup = dialog.findViewById<ChipGroup>(R.id.kmChipGroup)
            val colorChipGroup = dialog.findViewById<ChipGroup>(R.id.colorChipGroup)

            val txtTransmission = dialog.findViewById<TextView>(R.id.txtTransmission)
            val txtOwnership = dialog.findViewById<TextView>(R.id.txtOwnership)
            val txtBodyType = dialog.findViewById<TextView>(R.id.txtBodyType)
            val txtKms = dialog.findViewById<TextView>(R.id.txtKms)
            val txtColor = dialog.findViewById<TextView>(R.id.txtColor)

            dialog.findViewById<AppCompatButton>(R.id.btnApply).setOnClickListener {
                appliedFilter()
                dialog.hide()
            }

            txtTransmission.setOnClickListener {
                toggleVisibility(transmissionChipGroup, txtTransmission)
            }
            txtOwnership.setOnClickListener {
                toggleVisibility(ownerChipGroup, txtOwnership)
            }
            txtBodyType.setOnClickListener {
                toggleVisibility(bodyTypeChipGroup, txtBodyType)
            }
            txtKms.setOnClickListener {
                toggleVisibility(kmChipGroup, txtKms)
            }
            txtColor.setOnClickListener {
                toggleVisibility(colorChipGroup, txtColor)
            }

            //set Brand Data
            for (brand in catData) {
                val chip =
                    LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                chip.background = chipBorderDrawable
                chip.text = brand.category
                /*if (selectedValue["brand"]?.isNotEmpty() == true && selectedValue["brand"] == brand.category)
                    chip.isChecked = true*/

                chip.setOnClickListener {
                    for (j in 0 until brandChipGroup.childCount) {
                        val otherChip = brandChipGroup.getChildAt(j) as Chip
                        otherChip.isChecked = otherChip == chip
                    }
                    if (selectedValue["brand"] == chip.text.toString()) {
                        selectedValue["brand"] = ""
                        chip.isChecked = false
                        updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                    } else {
                        selectedValue["brand"] = chip.text.toString()
                        updateSelectedChip(selectedShipGroup, false, null)
                    }
                }



                brandChipGroup.addView(chip)
            }
            //Model Year
            for (year in StaticValue.chipYear) {
                val chip =
                    LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                chip.background = chipBorderDrawable
                chip.text = year
                /*if (selectedValue["model_year"]?.isNotEmpty() == true && selectedValue["model_year"] == year)
                    chip.isChecked = true*/
                chip.setOnClickListener {
                    for (j in 0 until modelYearChipGroup.childCount) {
                        val otherChip = modelYearChipGroup.getChildAt(j) as Chip
                        otherChip.isChecked = otherChip == chip
                    }
                    if (selectedValue["model_year"] == chip.text.toString()) {
                        selectedValue["model_year"] = ""
                        chip.isChecked = false
                        updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                    } else {
                        selectedValue["model_year"] = chip.text.toString()
                        updateSelectedChip(selectedShipGroup, false, null)
                    }
                }
                modelYearChipGroup.addView(chip)
            }
            //Price Range
            for (price in StaticValue.chipPrice) {
                val chip =
                    LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                chip.background = chipBorderDrawable
                chip.text = price
                /* if (selectedValue["price"]?.isNotEmpty() == true && selectedValue["price"] == price)
                     chip.isChecked = true*/
                chip.setOnClickListener {
                    for (j in 0 until priceRangeChipGroup.childCount) {
                        val otherChip = priceRangeChipGroup.getChildAt(j) as Chip
                        otherChip.isChecked = otherChip == chip
                    }

                    if (selectedValue["price"] == chip.text.toString()) {
                        selectedValue["price"] = ""
                        chip.isChecked = false
                        updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                    } else {
                        selectedValue["price"] = chip.text.toString()
                        updateSelectedChip(selectedShipGroup, false, null)
                    }

                }
                priceRangeChipGroup.addView(chip)
            }
            //KM Range
            for (kms in StaticValue.kmsPrice) {
                val chip =
                    LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                chip.background = chipBorderDrawable
                chip.text = kms

                /* if (selectedValue["km_driven"]?.isNotEmpty() == true && selectedValue["km_driven"] == kms)
                     chip.isChecked = true*/
                chip.setOnClickListener {
                    for (j in 0 until kmChipGroup.childCount) {
                        val otherChip = kmChipGroup.getChildAt(j) as Chip
                        otherChip.isChecked = otherChip == chip
                    }
                    if (selectedValue["km_driven"] == chip.text.toString()) {
                        selectedValue["km_driven"] = ""
                        chip.isChecked = false
                        updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                    } else {
                        selectedValue["km_driven"] = chip.text.toString()
                        updateSelectedChip(selectedShipGroup, false, null)
                    }
                }
                kmChipGroup.addView(chip)
            }

            for (filterLabel in filterLabelData) {
                log("Filter", " ${filterLabel.key}")
                when (filterLabel.key) {
                    "Body Type" -> {
                        val chip =
                            LayoutInflater.from(this)
                                .inflate(R.layout.chip_item, null, false) as Chip
                        chip.background = chipBorderDrawable
                        chip.text = filterLabel.value
                        /*  if (selectedValue["size"]?.isNotEmpty() == true && selectedValue["size"] == filterLabel.value)
                              chip.isChecked = true*/
                        chip.setOnClickListener {
                            for (j in 0 until bodyTypeChipGroup.childCount) {
                                val otherChip = bodyTypeChipGroup.getChildAt(j) as Chip
                                otherChip.isChecked = otherChip == chip
                            }
                            if (selectedValue["size"] == chip.text.toString()) {
                                selectedValue["size"] = ""
                                chip.isChecked = false
                                updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                            } else {
                                selectedValue["size"] = chip.text.toString()
                                updateSelectedChip(selectedShipGroup, false, null)
                            }
                        }
                        bodyTypeChipGroup.addView(chip)
                    }
                    "Fuel Type" -> {
                        val chip =
                            LayoutInflater.from(this)
                                .inflate(R.layout.chip_item, null, false) as Chip
                        chip.background = chipBorderDrawable
                        chip.text = filterLabel.value
                        /* if (selectedValue["fuel"]?.isNotEmpty() == true && selectedValue["fuel"] == filterLabel.value)
                             chip.isChecked = true*/
                        chip.setOnClickListener {
                            for (j in 0 until fuelChipGroup.childCount) {
                                val otherChip = fuelChipGroup.getChildAt(j) as Chip
                                otherChip.isChecked = otherChip == chip
                            }
                            if (selectedValue["fuel"] == chip.text.toString()) {
                                selectedValue["fuel"] = ""
                                chip.isChecked = false
                                updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                            } else {
                                selectedValue["fuel"] = chip.text.toString()
                                updateSelectedChip(selectedShipGroup, false, null)
                            }
                        }
                        fuelChipGroup.addView(chip)
                    }
                    "Transmission" -> {
                        val chip =
                            LayoutInflater.from(this)
                                .inflate(R.layout.chip_item, null, false) as Chip
                        chip.background = chipBorderDrawable
                        chip.text = filterLabel.value
                        /*if (selectedValue["transmission"]?.isNotEmpty() == true && selectedValue["transmission"] == filterLabel.value)
                            chip.isChecked = true*/
                        chip.setOnClickListener {
                            for (j in 0 until transmissionChipGroup.childCount) {
                                val otherChip = transmissionChipGroup.getChildAt(j) as Chip
                                otherChip.isChecked = otherChip == chip
                            }
                            if (selectedValue["transmission"] == chip.text.toString()) {
                                selectedValue["transmission"] = ""
                                chip.isChecked = false
                                updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                            } else {
                                selectedValue["transmission"] = chip.text.toString()
                                updateSelectedChip(selectedShipGroup, false, null)
                            }
                        }
                        transmissionChipGroup.addView(chip)
                    }
                    "Ownership" -> {
                        val chip =
                            LayoutInflater.from(this)
                                .inflate(R.layout.chip_item, null, false) as Chip
                        chip.background = chipBorderDrawable
                        chip.text = filterLabel.value
                        /*if (selectedValue["owner"]?.isNotEmpty() == true && selectedValue["owner"] == filterLabel.value)
                            chip.isChecked = true*/
                        chip.setOnClickListener {
                            for (j in 0 until ownerChipGroup.childCount) {
                                val otherChip = ownerChipGroup.getChildAt(j) as Chip
                                otherChip.isChecked = otherChip == chip
                            }
                            if (selectedValue["owner"] == chip.text.toString()) {
                                selectedValue["owner"] = ""
                                chip.isChecked = false
                                updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                            } else {
                                selectedValue["owner"] = chip.text.toString()
                                updateSelectedChip(selectedShipGroup, false, null)
                            }

                        }
                        ownerChipGroup.addView(chip)
                    }
                    "Color" -> {
                        val chip =
                            LayoutInflater.from(this)
                                .inflate(R.layout.chip_item, null, false) as Chip
                        chip.background = chipBorderDrawable
                        chip.text = filterLabel.value
                        /* if (selectedValue["color"]?.isNotEmpty() == true && selectedValue["color"] == filterLabel.value)
                             chip.isChecked = true*/
                        chip.setOnClickListener {
                            for (j in 0 until colorChipGroup.childCount) {
                                val otherChip = colorChipGroup.getChildAt(j) as Chip
                                otherChip.isChecked = otherChip == chip
                            }
                            if (selectedValue["color"] == chip.text.toString()) {
                                selectedValue["color"] = ""
                                chip.isChecked = false
                                updateSelectedChip(selectedShipGroup, true, chip.text.toString())
                            } else {
                                selectedValue["color"] = chip.text.toString()
                                updateSelectedChip(selectedShipGroup, false, null)
                            }
                        }
                        colorChipGroup.addView(chip)
                    }
                }
            }

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = (getScreenWidthSize(this) * 0.95).toInt()
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = lp
        } else {
            dialog.show()
        }
    }

    private fun updateSelectedChip(chipGroup: ChipGroup, isRemove: Boolean, text: String?) {
        if (!isRemove) {
            chipGroup.removeAllViews()
            for (map in selectedValue) {
                if (map.value.length > 2) {
                    val chip =
                        LayoutInflater.from(this).inflate(R.layout.chip_item, null, false) as Chip
                    chip.background = chipBorderDrawable
                    chip.text = map.value
                    chip.isClickable = false
                    chipGroup.addView(chip)
                }
            }
        } else {
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (text.equals(chip.text.toString(), ignoreCase = true)) {
                    chipGroup.removeViewAt(i)
                    return
                }
            }
        }
    }

    private fun appliedFilter() {
        log("appliedFilter ", " $selectedValue")
        viewModel.getProductFilterData(
            "Token ${
                PreferenceProvider(applicationContext).getStringValue(
                    PreferenceKey.APP_TOKEN
                )
            }", StaticValue.REST_ID, selectedValue
        )

    }

    // Function to toggle visibility of a view
    private fun toggleVisibility(view: View, textView: TextView) {
        if (view.isVisible) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_down,
                0
            )
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_up,
                0
            )
        }
    }

}

