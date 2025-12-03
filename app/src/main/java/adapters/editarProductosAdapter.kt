package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import entidades.Venta
import com.example.trabajomarketplace.R

class editarProductosAdapter(
    private val context: Context,
    private val ventas: List<Venta>,
    private val onEditClickListener: (Venta) -> Unit,
    private val onDeleteClickListener: (Venta) -> Unit
) : RecyclerView.Adapter<editarProductosAdapter.ProductoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(venta: Venta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_editarproductos, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(ventas[position])
    }

    override fun getItemCount(): Int = ventas.size

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagenProducto: ImageView = itemView.findViewById(R.id.imagenProductoEditar)
        private val precioProducto: TextView = itemView.findViewById(R.id.precioProducto)
        private val tituloProducto: TextView = itemView.findViewById(R.id.tituloProducto)
        private val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        private val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)

        fun bind(venta: Venta) {
            // Cargar la imagen de la venta, suponiendo que la imagen se encuentra en la primera posición de la lista de imágenes
            if (venta.imagenes.isNotEmpty()) {
                Glide.with(context)
                    .load(venta.imagenes[0])
                    .error(R.drawable.blankimage) // Establecer la imagen por defecto en caso de error
                    .into(imagenProducto)
            } else {
                Glide.with(context)
                    .load(venta.imagenes[0])
                    .error(R.drawable.blankimage) // Establecer la imagen por defecto en caso de error
                    .into(imagenProducto)
            }

            precioProducto.text = "$ ${venta.precio}"
            tituloProducto.text =  venta.titulo

            btnEditar.setOnClickListener {
                onEditClickListener(venta)
            }

            btnEliminar.setOnClickListener {
                onDeleteClickListener(venta)
            }
        }
    }
}