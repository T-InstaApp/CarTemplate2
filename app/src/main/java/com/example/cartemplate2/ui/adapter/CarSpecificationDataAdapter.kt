package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.CarSpecificationListItemBinding
import com.example.cartemplate2.datamodel.Specification
import com.example.cartemplate2.listener.RecyclerViewClickListener
import com.example.cartemplate2.utils.getImageID
import com.example.cartemplate2.utils.log

class CarSpecificationDataAdapter(
    var data: ArrayList<Specification>,
    var type: String,
    private val listener: RecyclerViewClickListener,
    private var size: Int
) :
    RecyclerView.Adapter<CarSpecificationDataAdapter.OrderViewHolder>() {

    override fun getItemCount() = size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.car_specification_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.carSpecificationListItemBinding.specificationLabel.text =
            data[position].addon_content_name
        holder.carSpecificationListItemBinding.specificationValue.text = data[position].values
        holder.carSpecificationListItemBinding.specificationIcon.setImageResource(getImageID(data[position].addon_content_name!!))

        holder.carSpecificationListItemBinding.imgView.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.carSpecificationListItemBinding.cardLayout,
                "${data[position].addon_content_name}#${data[position].extra_data}#" +
                        "${data[position].image_url}",
                "Inspection"
            )
        }

        if (type == "Inspection" && data[position].values!!.contains("Issue", ignoreCase = true)) {
            log(
                "onRecyclerItemClick ",
                "333 value= ${data[position].addon_content_name} = ${data[position].image_url}"
            )
            holder.carSpecificationListItemBinding.imgView.visibility = View.VISIBLE
        }
    }

    fun updateSize(count: Int) {
        size = count
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(
        val carSpecificationListItemBinding: CarSpecificationListItemBinding
    ) : RecyclerView.ViewHolder(carSpecificationListItemBinding.root)
}