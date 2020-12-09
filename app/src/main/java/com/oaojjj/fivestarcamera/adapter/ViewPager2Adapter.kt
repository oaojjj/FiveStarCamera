package com.oaojjj.fivestarcamera.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import com.oaojjj.fivestarcamera.controller.ImageController
import kotlinx.android.synthetic.main.item_image.view.*

@GlideModule
class ViewPager2Adapter(private var images: MutableList<PreviewImage>) :
    RecyclerView.Adapter<ViewPager2Adapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(index: Int) {
            /*if (image.bitmap == null)
                image.createBitmap()*/

            kotlin.run {
                Log.d("test", itemView.iv_image.width.toString())
                Log.d("test", itemView.iv_image.height.toString())
                Glide.with(itemView)
                    .asBitmap()
                    .signature(ObjectKey("signature string"))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                 /*   .override(
                        480,
                        720
                    ) // 이거 안해주면 슬라이드시 렉 엄청 걸림*/
                    .load(images[index].path)
                    .listener(
                        object : RequestListener<Bitmap> {
                            override fun onResourceReady(
                                resource: Bitmap?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                itemView.iv_image.post {
                                    images[index].bitmap = resource
                                    itemView.iv_image.setImageBitmap(resource)
                                }
                                return false
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Bitmap>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        }
                    ).submit()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.d("onBindViewHolder", position.toString())
        holder.onBind(position)
    }

    override fun getItemCount(): Int = images.size
    fun getItems() = images
}