package com.oaojjj.fivestarcamera.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.oaojjj.fivestarcamera.PreviewImage
import com.oaojjj.fivestarcamera.R
import kotlinx.android.synthetic.main.item_image.view.*

class ViewPager2Adapter(var images: MutableList<PreviewImage>) :
    RecyclerView.Adapter<ViewPager2Adapter.ImageViewHolder>() {
    private var currentImage: PreviewImage? = null

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(image: PreviewImage) {
            /*if (image.bitmap == null)
                image.createBitmap()*/

            Glide.with(itemView)
                .asBitmap()
                .load(image.path)
                .placeholder(R.drawable.ic_waiting)
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
                                currentImage?.bitmap = resource
                                currentImage?.path = image.path
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Log.d("onBindViewHolder", position.toString())
        holder.onBind(images[position])
    }

    override fun getItemCount(): Int = images.size

    fun getCurrentImage(): PreviewImage? = currentImage
}