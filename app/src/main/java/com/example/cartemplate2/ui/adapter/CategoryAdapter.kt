package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cartemplate2.R
import com.example.cartemplate2.databinding.CategoryListItemBinding
import com.example.cartemplate2.datamodel.CategoryResponse
import com.example.cartemplate2.listener.RecyclerViewClickListener

var selectedItem = 0

class CategoryAdapter(
    private val catData: List<CategoryResponse>, private val context: Context,
    private val listener: RecyclerViewClickListener
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun getItemCount() = catData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.category_list_item, parent, false
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.categoryListItemBinding.categoryData = catData[position]

        /* if (position == selectedItem) {
             holder.categoryListItemBinding.cardLayout.outlineSpotShadowColor =
                 (context.getColor(R.color.color_dark))
             holder.categoryListItemBinding.cardLayout.outlineAmbientShadowColor =
                 (context.getColor(R.color.color_dark))
             holder.categoryListItemBinding.cardLayout.strokeWidth = 3
             holder.categoryListItemBinding.txtCatName.setTextColor(context.getColor(R.color.color_dark))
         } else {
             holder.categoryListItemBinding.cardLayout.outlineSpotShadowColor =
                 (context.getColor(R.color.grey_light))
             holder.categoryListItemBinding.cardLayout.outlineAmbientShadowColor =
                 (context.getColor(R.color.grey_light))
             holder.categoryListItemBinding.cardLayout.strokeWidth = 0
             holder.categoryListItemBinding.txtCatName.setTextColor(context.getColor(R.color.grey_light))
         }*/

        holder.categoryListItemBinding.cardLayout.setOnClickListener {
            selectedItem = position
            notifyDataSetChanged()
            listener.onRecyclerItemClick(
                holder.categoryListItemBinding.cardLayout,
                "${catData[position].category_id}@${catData[position].category}", "Category"
            )
        }

    }

    inner class CategoryViewHolder(
        val categoryListItemBinding: CategoryListItemBinding
    ) : RecyclerView.ViewHolder(categoryListItemBinding.root)

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}