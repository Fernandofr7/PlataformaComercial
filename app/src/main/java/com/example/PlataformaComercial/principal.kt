package com.example.trabajomarketplace

import adapters.ProductoAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.AppDatabase
import entidades.Venta
import android.Manifest
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class principal : AppCompatActivity(), ProductoAdapter.OnItemClickListener {

    private lateinit var recyclerViewProductos: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val btn: Button = findViewById(R.id.btnpaginaVender)
        btn.setOnClickListener{
            val intent: Intent = Intent(this, ventas::class.java)
            startActivity(intent)
        }


        val btnCategories: Button = findViewById(R.id.button3)
        btnCategories.setOnClickListener{
            val intent: Intent = Intent(this, categorias::class.java)
            startActivity(intent)
        }

        val btnUser: ImageView = findViewById(R.id.imageView3)
        btnUser.setOnClickListener{
            val intent: Intent = Intent(this, usuario::class.java)
            startActivity(intent)
        }

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos)
        recyclerViewProductos.layoutManager = GridLayoutManager(this, 2)

        if (checkStoragePermission()) {
            cargarProductos()
        } else {
            requestStoragePermission()
        }

        val textView = findViewById<TextView>(R.id.textView)
        textView.setOnClickListener {
            cargarProductoses()
            Toast.makeText(this, "Cargando datos...", Toast.LENGTH_SHORT).show()
        }
        //cargarProductoses()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Salir de la aplicación")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { dialog, which ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()
    }



    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarProductos()
            } else {
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun cargarProductos() {
        val db = AppDatabase.getDatabase(this)
        val ventaDao = db.ventaDao()

        val categoria = intent.getStringExtra("categoria")

        Thread {
            val ventas = if (categoria != null) {
                ventaDao.filtrarVentasPorCategoria(categoria)
            } else {
                ventaDao.obtenerVentas()
            }
            runOnUiThread {
                productoAdapter = ProductoAdapter(this, ventas, this)
                recyclerViewProductos.adapter = productoAdapter
            }
        }.start()
    }


    private fun cargarProductoses() {
        val db = AppDatabase.getDatabase(this)
        val ventaDao = db.ventaDao()

        Thread {
            val ventas = ventaDao.obtenerVentas()
            ventas.forEach { venta ->
                Log.d("cargarProductos", "Venta: ${venta.titulo}, Imagenes: ${venta.imagenes}")
            }
            runOnUiThread {
                productoAdapter = ProductoAdapter(this, ventas, this)
                recyclerViewProductos.adapter = productoAdapter
            }
        }.start()
    }

    override fun onItemClick(venta: Venta) {
        val intent = Intent(this, detalleProductos::class.java)
        intent.putExtra("venta", venta)
        startActivity(intent)
    }
}