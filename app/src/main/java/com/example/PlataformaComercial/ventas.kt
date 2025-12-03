package com.example.trabajomarketplace

import adapters.ImageAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import database.AppDatabase
import entidades.Venta
import models.ImageModel
import java.io.FileInputStream

class ventas : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val images = mutableListOf<ImageModel>()

    val Id = GlobalValues.userId
    val Name = GlobalValues.userName
    val Apellido = GlobalValues.userApellido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        /*
        println("id: $Id")
        println("Nombre: $Name")
        println("Apellido: $Apellido")*/


        val tvNombreUsuario: TextView = findViewById(R.id.textObtenerNombre)
        tvNombreUsuario.text = Name+" "+Apellido

        //para la imagen
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter(images)
        recyclerView.adapter = imageAdapter

        val btnSelectImages: Button = findViewById(R.id.btnSelectImages)
        btnSelectImages.setOnClickListener {
            selectImages()
        }

        // Referencia al Spinner
        val spinner: Spinner = findViewById(R.id.categoriaVentas)
        // Crear un ArrayAdapter usando el array de strings y un layout predeterminado de spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.categorias_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando la lista de opciones aparece
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            spinner.adapter = adapter
        }

        val regresar: ImageView = findViewById(R.id.flechalogin)
        regresar.setOnClickListener{
            val intent: Intent = Intent(this, principal::class.java)
            startActivity(intent)
            finish()
        }


        val editar = intent.getBooleanExtra("EDITAR", false)

        val btnGuardarVenta: TextView = findViewById(R.id.textPublicar)


        // Verificar si se está editando o creando una venta

        if (editar) {
            val venta: Venta = intent.getSerializableExtra("VENTA") as Venta
            val headerTitle: TextView = findViewById(R.id.textView2)
            headerTitle.text = "Editar Publicación"

            btnGuardarVenta.setOnClickListener {
                editarVenta()
            }

            val btnGuardarVenta: TextView = findViewById(R.id.textPublicar)
            btnGuardarVenta.text = "Guardar Cambios"

            // Establecer los valores en los campos de entrada
            val etTitulo: EditText = findViewById(R.id.tituloVentas)
            etTitulo.setText(venta.titulo)

            val etPrecio: EditText = findViewById(R.id.precioVentas)
            etPrecio.setText(venta.precio.toString())

            val spCategoria: Spinner = findViewById(R.id.categoriaVentas)
            val categoriaIndex = resources.getStringArray(R.array.categorias_array).indexOf(venta.categoria)
            spCategoria.setSelection(categoriaIndex)

            val etUbicacion: EditText = findViewById(R.id.ubicacionVentas)
            etUbicacion.setText(venta.ubicacion)

            val etEstado: EditText = findViewById(R.id.estadoVentas)
            etEstado.setText(venta.estado)

            val etDescripcion: EditText = findViewById(R.id.descripcionVentas)
            etDescripcion.setText(venta.descripcion)

            // Cargar las imágenes de la venta en el RecyclerView
            images.clear()
            venta.imagenes.forEach { uriString ->
                val uri = Uri.parse(uriString)
                images.add(ImageModel(uri))
            }
            imageAdapter.notifyDataSetChanged()

        }else{
            btnGuardarVenta.setOnClickListener {
                guardarVenta()
            }
        }
    }

    private fun selectImages() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.clipData?.let {
                for (i in 0 until it.itemCount) {
                    val uri = it.getItemAt(i).uri
                    images.add(ImageModel(uri))
                }
            } ?: data?.data?.let {
                images.add(ImageModel(it))
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun guardarVenta() {

        val etTitulo: EditText = findViewById(R.id.tituloVentas)
        val etPrecio: EditText = findViewById(R.id.precioVentas)
        val spCategoria: Spinner = findViewById(R.id.categoriaVentas)
        val etUbicacion: EditText = findViewById(R.id.ubicacionVentas)
        val etEstado: EditText = findViewById(R.id.estadoVentas)
        val etDescripcion: EditText = findViewById(R.id.descripcionVentas)

        val titulo = etTitulo.text.toString().trim()
        val precioText = etPrecio.text.toString().trim()
        val categoria = spCategoria.selectedItem.toString().trim()
        val ubicacion = etUbicacion.text.toString().trim()
        val estado = etEstado.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val imagenesUris = images.map { it.uri.toString() }

        // Validar campos
        if (titulo.isBlank() || precioText.isBlank() || categoria.isBlank() || ubicacion.isBlank() || estado.isBlank() || descripcion.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (imagenesUris.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona al menos una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = try {
            precioText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor, introduce un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Imprimir datos en la consola
        println("Venta a guardar:")
        println("Título: $titulo")
        println("Precio: $precio")
        println("Categoría: $categoria")
        println("Ubicación: $ubicacion")
        println("Estado: $estado")
        println("Descripción: $descripcion")
        println("Imágenes: $imagenesUris")

        val venta = Venta(
            titulo = titulo,
            precio = precio,
            categoria = categoria,
            ubicacion = ubicacion,
            estado = estado,
            descripcion = descripcion,
            imagenes = imagenesUris,
            idUsuarioPer = Id
        )

        // Guardar venta en la base de datos
        val db = AppDatabase.getDatabase(this)
        val ventaDao = db.ventaDao()
            Thread {
                ventaDao.insertarVenta(venta)
                runOnUiThread {
                    Toast.makeText(this, "Publicación guardada exitosamente", Toast.LENGTH_SHORT).show()
                    // Limpiar campos después de guardar
                    etTitulo.setText("")
                    etPrecio.setText("")
                    etUbicacion.setText("")
                    etEstado.setText("")
                    etDescripcion.setText("")
                    images.clear()
                    imageAdapter.notifyDataSetChanged()
                }


            }.start()


    }

    private fun editarVenta() {

        val etTitulo: EditText = findViewById(R.id.tituloVentas)
        val etPrecio: EditText = findViewById(R.id.precioVentas)
        val spCategoria: Spinner = findViewById(R.id.categoriaVentas)
        val etUbicacion: EditText = findViewById(R.id.ubicacionVentas)
        val etEstado: EditText = findViewById(R.id.estadoVentas)
        val etDescripcion: EditText = findViewById(R.id.descripcionVentas)

        val titulo = etTitulo.text.toString().trim()
        val precioText = etPrecio.text.toString().trim()
        val categoria = spCategoria.selectedItem.toString().trim()
        val ubicacion = etUbicacion.text.toString().trim()
        val estado = etEstado.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val imagenesUris = images.map { it.uri.toString() }

        // Validar campos
        if (titulo.isBlank() || precioText.isBlank() || categoria.isBlank() || ubicacion.isBlank() || estado.isBlank() || descripcion.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (imagenesUris.isEmpty()) {
            Toast.makeText(this, "Por favor, selecciona al menos una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = try {
            precioText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor, introduce un precio válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Imprimir datos en la consola
        println("Venta a guardar:")
        println("Título: $titulo")
        println("Precio: $precio")
        println("Categoría: $categoria")
        println("Ubicación: $ubicacion")
        println("Estado: $estado")
        println("Descripción: $descripcion")
        println("Imágenes: $imagenesUris")
        val ventaId = intent.getIntExtra("VENTA_ID", -1)
        if (ventaId == -1) {
            Toast.makeText(this, "ID de venta no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val  venta = Venta(
            id = ventaId,
            titulo = titulo,
            precio = precio,
            categoria = categoria,
            ubicacion = ubicacion,
            estado = estado,
            descripcion = descripcion,
            imagenes = imagenesUris,
            idUsuarioPer = Id
        )

        // Guardar venta en la base de datos
        val db = AppDatabase.getDatabase(this)
        val ventaDao = db.ventaDao()

        Thread {
            ventaDao.actualizarVenta(venta)
            runOnUiThread {
                Toast.makeText(this, "Publicación editada exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar campos después de guardar
                etTitulo.setText("")
                etPrecio.setText("")
                etUbicacion.setText("")
                etEstado.setText("")
                etDescripcion.setText("")
                images.clear()
                imageAdapter.notifyDataSetChanged()

                val intent = Intent(this, usuario::class.java)
                startActivity(intent)
                finish()
            }

        }.start()

    }


    companion object {
        private const val REQUEST_CODE = 100
    }
}