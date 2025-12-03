package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabajomarketplace.R

data class Categoria(val nombre: String, val icono: Int)

class CategoriaAdapter(private val categorias: List<Categoria>, private val listener: OnItemClickListener) : RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(categoria: Categoria)
    }

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageViewIcono: ImageView = itemView.findViewById(R.id.imageViewIcono)
        var textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)

        fun bind(categoria: Categoria, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClick(categoria)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return CategoriaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoria = categorias[position]
        holder.textViewNombre.text = categoria.nombre
        holder.imageViewIcono.setImageResource(categoria.icono)
        holder.bind(categoria, listener)
    }

    override fun getItemCount() = categorias.size
}


