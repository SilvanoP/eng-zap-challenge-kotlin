package br.com.desafio.grupozap.features.detail

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Picasso

class RealStateViewPagerAdapter(var imagesURL: List<String>): PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean  = view == `object` as ImageView

    override fun getCount(): Int  = imagesURL.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Picasso.get().load(imagesURL[position]).into(imageView)
        container.addView(imageView,0)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

    fun swapImages(newImagesUrl: List<String>) {
        imagesURL = newImagesUrl
        notifyDataSetChanged()
    }
}