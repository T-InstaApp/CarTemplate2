package com.example.cartemplate2.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ActivityTestDriveBookingListBinding
import com.example.cartemplate2.databinding.ProgressDialogLayoutBinding
import com.example.cartemplate2.databinding.ToolbarBinding
import com.example.cartemplate2.datamodel.TestDriveBooking
import com.example.cartemplate2.listener.NetworkCallListener
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.model.HomeViewModel
import com.example.cartemplate2.provider.HomeViewModelFactory
import com.example.cartemplate2.ui.adapter.TestDriveBookingListDataAdapter
import com.example.cartemplate2.utils.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class TestDriveBookingListFragment : Fragment(), NetworkCallListener, RecyclerViewClickListener,
    KodeinAware {
    override val kodein by kodein()
    private var _binding: ActivityTestDriveBookingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: ToolbarBinding
    private lateinit var progressLayout: ProgressDialogLayoutBinding

    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityTestDriveBookingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.homeListener = this
        toolbar = binding.toolBar
        progressLayout = binding.progressBar

        toolbar.txtTopHeading.text = "Test Drive Booking List"

        toolbar.icMenu.setImageResource(R.drawable.ic_back)
        toolbar.icMenu.setOnClickListener { requireActivity().onBackPressed() }

        loadData()
    }

    private fun loadData() {
        viewModel.getTestDriveBookingData(
            "Token " + PreferenceProvider(requireContext()).getStringValue(PreferenceKey.APP_TOKEN)!!,
            PreferenceProvider(requireContext()).getIntValue(PreferenceKey.USER_ID).toString(),
        )
    }

    override fun onStarted() {
        progressLayout.progressLayout.visible()
    }

    override fun onFailure(message: String, type: String) {
        progressLayout.progressLayout.notVisible()
        when (type) {
            CommonKey.ERROR_CODE_401 -> {
                dialogSessionExpire(requireContext())
            }
            else -> showAlert(requireActivity(), getString(R.string.alert), message)
        }
    }

    override fun <T> onSuccess(dataG: T, type: String) {
        progressLayout.progressLayout.notVisible()
        if (type == "getTestDriveBookingData") {
            val data: ArrayList<TestDriveBooking> = dataG as ArrayList<TestDriveBooking>
            binding.productList.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            binding.productList.setHasFixedSize(true)
            binding.productList.adapter =
                TestDriveBookingListDataAdapter(data, this, requireContext())
        } else if (type == "updateTestDriveBookingStatus") {
            Toast.makeText(
                requireContext(),
                "Your test drive booking status has been successfully updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun <T> onRecyclerItemClick(view: View, dataG: T, status: String) {
        if (status == "Update") {
            viewModel.updateTestDriveBookingStatus(
                "Token ${
                    PreferenceProvider(requireContext()).getStringValue(
                        PreferenceKey.APP_TOKEN
                    )
                }",
                dataG.toString().split("@")[0].toInt(),
                dataG.toString().split("@")[1].toBoolean()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
