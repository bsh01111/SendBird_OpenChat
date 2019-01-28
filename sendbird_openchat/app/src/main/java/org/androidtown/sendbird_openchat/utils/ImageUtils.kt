package org.androidtown.sendbird_openchat.utils

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget

class ImageUtils {

    companion object {
        /**
         * Crops image into a circle that fits within the ImageView.
         */
        fun displayRoundImageFromUrl(context: Context, url: String, imageView: ImageView) {
            val myOptions = RequestOptions()
                .centerCrop()
                .dontAnimate()

            Glide.with(context)
                .asBitmap()
                .apply(myOptions)
                .load(url)
                .into(object : BitmapImageViewTarget(imageView){
                    override fun setResource(resource: Bitmap?) {
                        var circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        imageView.setImageDrawable(circularBitmapDrawable)
                    }
                })
        }
    }
}