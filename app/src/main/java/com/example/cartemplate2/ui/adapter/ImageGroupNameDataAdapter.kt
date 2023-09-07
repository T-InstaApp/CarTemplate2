package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartemplate2.ui.details.ImageUrlWithGroupName
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.ImageGroupNameListItemBinding
import com.example.cartemplate2.listener.RecyclerViewClickListener

class ImageGroupNameDataAdapter(
    var orderMenuData: List<ImageUrlWithGroupName>,
    private val listener: RecyclerViewClickListener,
    val context: Context
) :
    RecyclerView.Adapter<ImageGroupNameDataAdapter.OrderViewHolder>() {
    private var selectedItemPosition: Int = 0


    override fun getItemCount() = orderMenuData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OrderViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.image_group_name_list_item, parent, false
        )
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: OrderViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = orderMenuData[position]


        // Set the text color based on the selected item
        val textColor = if (position == selectedItemPosition) {
            // Change to the desired color for the selected item
            ContextCompat.getColor(context, R.color.teal)
        } else {
            // Change to the desired color for the non-selected items
            ContextCompat.getColor(context, R.color.grey)
        }
        holder.imageGroupNameListItemBinding.txtGroupName.setTextColor(textColor)

        holder.imageGroupNameListItemBinding.txtGroupName.text = item.gName
        Glide.with(context).load(item.url).into(holder.imageGroupNameListItemBinding.image)

        holder.imageGroupNameListItemBinding.cardLayout.setOnClickListener {
            selectedItemPosition = position
            notifyDataSetChanged()
            listener.onRecyclerItemClick(
                holder.imageGroupNameListItemBinding.cardLayout,
                item.gName,"Image"
            )

            // Update the selection status and notify the adapter

        }
    }

    inner class OrderViewHolder(
        val imageGroupNameListItemBinding: ImageGroupNameListItemBinding
    ) : RecyclerView.ViewHolder(imageGroupNameListItemBinding.root)
}