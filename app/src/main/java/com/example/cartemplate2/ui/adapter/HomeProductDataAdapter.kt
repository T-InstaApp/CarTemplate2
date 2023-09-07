package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.TopCarListItemBinding
import com.example.cartemplate2.datamodel.ProductDataMode
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.utils.log

class HomeProductDataAdapter(
    var data: ArrayList<ProductDataMode>,
    private val listener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<HomeProductDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.top_car_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.topCarListItemBinding.menuData = data[position]
//        if (data[position].product_specification != null && data[position].product_specification!!.isNotEmpty()) {
//            log("HomeFragment ", "${data[position].product_specification!!["KM Driven"]}")
//            holder.topCarListItemBinding.txtKM.text =
//                data[position].product_specification!!["KM Driven"]!!.toString() + " Km"
//            holder.topCarListItemBinding.txtRegistrationDate.text =
//                data[position].product_specification!!["Registration Date"]!!.toString()
//            holder.topCarListItemBinding.txtOwnerShip.text =
//                data[position].product_specification!!["Ownership"]!!.toString()
//        }

        holder.topCarListItemBinding.cardLayout.setOnClickListener {
            selectedItem = position
            listener.onRecyclerItemClick(
                holder.topCarListItemBinding.cardLayout,
                "${data[position].product_name}@${data[position].product_id}@" +
                        "${data[position].price}@${data[position].category!!.category_name}@${data[position].product_size}",
                "Details"
            )
        }
    }

    inner class OrderViewHolder(
        val topCarListItemBinding: TopCarListItemBinding
    ) : RecyclerView.ViewHolder(topCarListItemBinding.root)
}