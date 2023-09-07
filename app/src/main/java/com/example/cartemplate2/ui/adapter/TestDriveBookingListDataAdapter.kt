package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.TestDriveListItem2Binding
import com.example.cartemplate2.datamodel.TestDriveBooking
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.utils.*

class TestDriveBookingListDataAdapter(
    var data: ArrayList<TestDriveBooking>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<TestDriveBookingListDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.test_drive_list_item_2, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.testDriveListItem2Binding.menuData = data[position]
        var discount = 10
        if (position % 2 == 0)
            discount = 20

        holder.testDriveListItem2Binding.txtOff.text = "${discount}% Off"
        val price = (data[position].price!!).toDouble()
        val discountAmount = (price * discount) / 100
        val finalPrice = price - discountAmount

        holder.testDriveListItem2Binding.txtDiscountPrice.text =
            "${PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(finalPrice.toLong())
            }"

        if (data[position].door_step)
            holder.testDriveListItem2Binding.txtAddress.text =
                (data[position].complete_address!!.replace("\n", ""))
        else
            holder.testDriveListItem2Binding.txtAddress.text = "Company Hub Address"

        holder.testDriveListItem2Binding.switchcompat.isChecked = data[position].booking_status

        if (data[position].product_specification != null && data[position].product_specification!!.isNotEmpty()) {
            holder.testDriveListItem2Binding.txtSpecification.text =
                "${data[position].product_specification!!["KM Driven"]!!.toString() + " Km"} ${StaticValue.DOT} " +
                        "${data[position].product_specification!!["Ownership"]!!} ${StaticValue.DOT}" +
                        (data[position].product_specification!!["Registration Date"]!!.toString())
        }


        holder.testDriveListItem2Binding.switchcompat.setOnCheckedChangeListener { _, isChecked ->
            listener.onRecyclerItemClick(
                holder.testDriveListItem2Binding.cardLayout,
                "${data[position].booking_id}@$isChecked",
                "Update"
            )
        }

        holder.testDriveListItem2Binding.cardLayout.setOnClickListener {
            selectedItem = position
            listener.onRecyclerItemClick(
                holder.testDriveListItem2Binding.cardLayout,
                "${data[position].product_name}@${data[position].product_id}@" +
                        "${data[position].price}",
                "Details"
            )
        }
    }

    inner class OrderViewHolder(
        val testDriveListItem2Binding: TestDriveListItem2Binding
    ) : RecyclerView.ViewHolder(testDriveListItem2Binding.root)
}