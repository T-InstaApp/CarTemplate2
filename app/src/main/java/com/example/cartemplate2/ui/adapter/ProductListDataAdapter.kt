package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ProductListItem2Binding
import com.example.cartemplate2.datamodel.ProductDataMode
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.utils.*

class ProductListDataAdapter(
    var data: ArrayList<ProductDataMode>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<ProductListDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_list_item_2, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.productListItemBinding.menuData = data[position]
        var discount = 10
        if (position % 2 == 0)
            discount = 20

        holder.productListItemBinding.txtOff.text = "${discount}% Off"
        val price = (data[position].price!!).toDouble()
        val discountAmount = (price * discount) / 100
        val finalPrice = price - discountAmount

        holder.productListItemBinding.txtDiscountPrice.text =
            "${PreferenceProvider(context).getStringValue(PreferenceKey.CURRENCY_SYMBOL)} ${
                formatNumberToWord(finalPrice.toLong())
            }"

        try {
            if (data[position].product_specification != null && data[position].product_specification!!.isNotEmpty()) {
                log("HomeFragment ", "${data[position].product_specification!!["KM Driven"]}")
                holder.productListItemBinding.txtSpecification.text =
                    "${data[position].product_specification!!["KM Driven"]!!.toString() + " Km"} ${StaticValue.DOT} " +
                            "${data[position].product_specification!!["Ownership"]!!} ${StaticValue.DOT}" +
                            (data[position].product_specification!!["Registration Date"]!!.toString())
            }
        } catch (ignore: Exception) {
        }
        holder.productListItemBinding.cardLayout.setOnClickListener {
            selectedItem = position
            listener.onRecyclerItemClick(
                holder.productListItemBinding.cardLayout,
                "${data[position].product_name}@${data[position].product_id}@" +
                        "${data[position].price}@${data[position].category!!.category_name}",
                "Details"
            )
        }
    }

    inner class OrderViewHolder(
        val productListItemBinding: ProductListItem2Binding
    ) : RecyclerView.ViewHolder(productListItemBinding.root)
}