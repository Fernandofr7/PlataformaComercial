package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import entidades.Venta
import com.example.trabajomarketplace.R

class ProductoAdapter(private val context: Context, private val productos: List<Venta>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(venta: Venta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.bind(producto, itemClickListener)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenProducto: ImageView = itemView.findViewById(R.id.imagenProductoEditar)
        private val precioProducto: TextView = itemView.findViewById(R.id.precioProducto)

        fun bind(venta: Venta, clickListener: OnItemClickListener) {
            if (venta.imagenes.isNotEmpty()) {
                // Si el producto tiene imágenes, carga la primera imagen
                Glide.with(itemView.context).load(venta.imagenes[0]).into(imagenProducto)
            } else {
                // Si el producto no tiene imágenes, carga la imagen por defecto
                Glide.with(itemView.context).load(R.drawable.unnamed).into(imagenProducto)
            }

            precioProducto.text = "$: ${venta.precio} ${venta.titulo}"
            itemView.setOnClickListener {
                clickListener.onItemClick(venta)
            }
        }
    }
}