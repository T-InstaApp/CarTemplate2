package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.BookingListItemBinding
import com.example.cartemplate2.datamodel.ProductBookingListDataModel
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.utils.PreferenceKey
import com.example.cartemplate2.utils.PreferenceProvider
import com.example.cartemplate2.utils.StaticValue
import com.example.cartemplate2.utils.formatNumberToWord

class BookingListDataAdapter(
    var data: ArrayList<ProductBookingListDataModel>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<BookingListDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.booking_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bookingListItemBinding.menuData = data[position]
        var discount = 10
        if (position % 2 == 0)
            discount = 20

        holder.bookingListItemBinding.txtOff.text = "${discount}% Off"
        val price = (data[position].product!!.price!!).toDouble()
        val discountAmount = (price * discount) / 100
        val finalPrice = price - discountAmount

        holder.bookingListItemBinding.txtDiscountPrice.text =
            "${PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(finalPrice.toLong())
            }"

        holder.bookingListItemBinding.txBookingAmount.text =
            "Booking Amount - ${PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord((data[position].total!!.split(".")[0]).toLong())
            }"

        if (data[position].product_specification != null && data[position].product_specification!!.isNotEmpty()) {
            holder.bookingListItemBinding.txtSpecification.text =
                "${data[position].product_specification!!["KM Driven"]!!.toString() + " Km"} ${StaticValue.DOT} " +
                        "${data[position].product_specification!!["Ownership"]!!} ${StaticValue.DOT}" +
                        (data[position].product_specification!!["Registration Date"]!!.toString())
        }

    }

    inner class OrderViewHolder(
        val bookingListItemBinding: BookingListItemBinding
    ) : RecyclerView.ViewHolder(bookingListItemBinding.root)
}