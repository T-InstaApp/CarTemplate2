package com.example.cartemplate2.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.FragmentHomeBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.datamodel.ProductDataMode
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.model.HomeViewModel
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.ui.adapter.CategoryAdapter
import com.example.cartemplate2.ui.adapter.HomeProductDataAdapter
import com.example.cartemplate2.ui.details.DetailsActivity
import com.example.cartemplate2.ui.details.ProductListActivity
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), NetworkCallListener, KodeinAware, RecyclerViewClickListener {
    override val kodein by kodein()
    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    lateinit var adapter: HomeProductDataAdapter
    lateinit var progressLayout: ProgressDialogLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        viewModel.homeListener = this
        progressLayout = _binding!!.progressBar


        _binding!!.txtViewAll.setOnClickListener {
            val intent = Intent(requireContext(), ProductListActivity::class.java)
            intent.putExtra("type", "All Category")
            intent.putExtra("name", "Show ALl Cars")
            intent.putExtra("id", "1")
            startActivity(intent)
        }

        viewModel.getMasterCat(
            "Token " + PreferenceProvider(requireContext()).getStringValue(
                PreferenceKey.APP_TOKEN
            )!!, "225"
        )

        initSearchView()
        loadCategoryData()
        loadTopCarData()
        loadHotDeals()
        return _binding!!.root
    }

    private fun loadCategoryData() {
        viewModel.getCategory(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID
        )
        viewModel.categoryData.observe(viewLifecycleOwner) { catData ->
            _binding!!.recyclerViewCategory.also {
                it.layoutManager =
                    LinearLayoutManager(
                        requireActivity().applicationContext,
                        RecyclerView.HORIZONTAL,
                        false
                    )
                it.setHasFixedSize(true)
                it.adapter = CategoryAdapter(catData, requireContext(), this)
            }
        }
    }

    private fun loadTopCarData() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "Top", ""
        )

    }

    private fun loadHotDeals() {
        viewModel.getProductData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            StaticValue.REST_ID,
            "new_arrievals", ""
        )
    }

    override fun onStarted() {
        progressLayout.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        log("HomeFragment", " onFailure $type data $message")
        progressLayout.progressLayout.notVisible()
        if (isAdded)
            when (type) {
                CommonKey.ERROR_CODE_401 -> {
                    dialogSessionExpire(
                        requireContext(),
                    )
                }
                else -> showAlert(
                    requireActivity(),
                    getString(R.string.alert),
                    message
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> onSuccess(dataG: T, type: String) {
        progressLayout.progressLayout.notVisible()
        if (isAdded)
            if (type == "Top") {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                _binding!!.recyclerTopCars.layoutManager =
                    LinearLayoutManager(requireActivity().baseContext, RecyclerView.VERTICAL, false)

                _binding!!.recyclerTopCars.setHasFixedSize(true)

                adapter = HomeProductDataAdapter(data, this)
                _binding!!.recyclerTopCars.adapter = adapter
            } else if (type == "new_arrievals") {
                val data: ArrayList<ProductDataMode> = dataG as ArrayList<ProductDataMode>
                _binding!!.recyclerNewAdded.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                _binding!!.recyclerNewAdded.setHasFixedSize(true)
                adapter = HomeProductDataAdapter(data, this)
                _binding!!.recyclerNewAdded.adapter = adapter
            }

    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {

        Log.d("TAG", "onRecyclerItemClick: $dataG")
        if (status == "Details") {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra("name", dataG.toString().split("@")[0])
            intent.putExtra("id", dataG.toString().split("@")[1])
            intent.putExtra("price", dataG.toString().split("@")[2])
            intent.putExtra("brand", dataG.toString().split("@")[3])
            startActivity(intent)
        } else if (status == "Category") {
            val intent = Intent(activity, ProductListActivity::class.java)
            intent.putExtra("name", dataG.toString().split("@")[1])
            intent.putExtra("id", dataG.toString().split("@")[0])
            intent.putExtra("type", "Other")
            startActivity(intent)
        }
    }

    private fun initSearchView() {
        _binding!!.idSV.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().trim()
                if (searchText.length > 2) {
                    val imm =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(_binding!!.idSV.windowToken, 0)
                    val intent = Intent(requireContext(), ProductListActivity::class.java)
                    intent.putExtra("type", "Search")
                    intent.putExtra("name", _binding!!.idSV.text.toString())
                    intent.putExtra("id", "1")
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }

}