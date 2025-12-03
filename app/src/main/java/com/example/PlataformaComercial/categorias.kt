package com.example.trabajomarketplace

import adapters.Categoria
import adapters.CategoriaAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class categorias : AppCompatActivity(), CategoriaAdapter.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar)

        val regresar: ImageView = findViewById(R.id.flechaprincpal)
        regresar.setOnClickListener{
            val intent: Intent = Intent(this, principal::class.java)
            startActivity(intent)
            finish()
        }

        val categorias = listOf(
            Categoria("Vehículos", R.drawable.vehiculo),
            Categoria("Inmuebles", R.drawable.inmuebles),
            Categoria("Electrónica", R.drawable.electronica),
            Categoria("Ropa y accesorios", R.drawable.ropa),
            Categoria("Hogar y jardín", R.drawable.hogar),
            Categoria("Ocio y entretenimiento", R.drawable.entretenimiento),
            Categoria("Deportes y aire libre", R.drawable.deportes),
            Categoria("Artículos para bebés y niños", R.drawable.ninos),
            Categoria("Salud y belleza", R.drawable.salud),
            Categoria("Negocios e industria", R.drawable.negocios),
            Categoria("Mascotas", R.drawable.mascotas),
            Categoria("Servicios", R.drawable.servicios),
            Categoria("Trabajo", R.drawable.trabajo),
            Categoria("Clases y cursos", R.drawable.clases),
            Categoria("Otros", R.drawable.otros)
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCategorias)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CategoriaAdapter(categorias, this)
    }

    override fun onItemClick(categoria: Categoria) {
        Toast.makeText(this, "Clicked on: ${categoria.nombre}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, principal::class.java)
        intent.putExtra("categoria", categoria.nombre)
        startActivity(intent)
    }

}