package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.TopCarListItemBinding
import com.example.cartemplate2.datamodel.MenuResponseData
import com.example.cartemplate2.listener.RecyclerViewClickListener

class TopCarDataAdapter(
    private val context: Context,
    private val listener: RecyclerViewClickListener,
) :
    PagingDataAdapter<MenuResponseData, TopCarDataAdapter.MenuViewHolder>(COMPARATOR) {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        //  holder.foodListItemBinding.menuData = getItem(position)!!

        Log.d("TAG", "onBindViewHolder: ${getItem(position)!!.brand}")
        holder.foodListItemBinding.cardLayout.setOnClickListener {
            listener.onRecyclerItemClick(
                holder.foodListItemBinding.cardLayout,
                "${getItem(position)!!.product_name}@${getItem(position)!!.product_id}" +
                        "@${getItem(position)!!.price}@${getItem(position)!!.brand}",
                "Details"
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.top_car_list_item, parent, false
        )
    )


    inner class MenuViewHolder(
        val foodListItemBinding: TopCarListItemBinding
    ) : RecyclerView.ViewHolder(foodListItemBinding.root)

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<MenuResponseData>() {
            override fun areItemsTheSame(
                oldItem: MenuResponseData,
                newItem: MenuResponseData
            ): Boolean {
                return oldItem.product_id == newItem.product_id
            }

            override fun areContentsTheSame(
                oldItem: MenuResponseData,
                newItem: MenuResponseData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}