package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.ImageModel
import com.example.trabajomarketplace.R

class DetallesImageAdapter (private val images: List<ImageModel>, private val onImageClick: (String) -> Unit) : RecyclerView.Adapter<DetallesImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView.context).load(image.uri).into(holder.imageView)
        holder.itemView.setOnClickListener { onImageClick(image.uri.toString()) }


    }

    override fun getItemCount(): Int = images.size
}