package com.uoocuniversity.tmf_flutter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class TmfAsyncDrawable : Drawable() {
    private lateinit var mCurrDrawable: Drawable

    override fun draw(canvas: Canvas) {
        if (::mCurrDrawable.isInitialized) mCurrDrawable.draw(canvas)
    }

    fun loadImage(context: Context, uri: String): TmfAsyncDrawable {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                   return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        mCurrDrawable = BitmapDrawable(context.resources, resource)
                        mCurrDrawable.bounds = bounds
                        invalidateSelf()
                        return true
                    } else {
                        return false
                    }
                }

            })
            .submit()
        return this
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSPARENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity() = PixelFormat.TRANSPARENT

}