package com.example.cartemplate2.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.cartemplate2.ui.details.ZoomImageActivity
import com.example.cartemplate2.R

class BannerViewPagerAdapter(
    private val context: Context,
    val images: ArrayList<String>,
    val type: String,
    val productID: Int,
    val productName: String
) :
    PagerAdapter() {

    private var inflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    @SuppressLint("MissingInflatedId")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.imageb_anner, null)
        val bannerItem = view.findViewById<ImageView>(R.id.bannerItem)
        if (type == "Zoom" || type == "InspectionReport") {
            val photoView =
                view.findViewById<com.github.chrisbanes.photoview.PhotoView>(R.id.photo_view)
            Glide.with(context).load(images[position]).into(photoView)
            photoView.visibility = View.VISIBLE
            bannerItem.visibility = View.GONE
        } else {
            Glide.with(context).load(images[position]).into(bannerItem)
        }
        val vp = container as ViewPager
        vp.addView(view, 0)

        bannerItem.setOnClickListener {
            if (type == "Details") {
                val intent = Intent(context, ZoomImageActivity::class.java)
                intent.putExtra("id", productID.toString())
                intent.putExtra("name", productName)
                context.startActivity(intent)
            }
        }
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}