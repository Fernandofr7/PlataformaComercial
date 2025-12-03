package com.example.trabajomarketplace

import adapters.DetallesImageAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import entidades.Venta
import models.ImageModel

class detalleProductos : AppCompatActivity() {

    val Name = GlobalValues.userName
    val Apellido = GlobalValues.userApellido
    val Telefono = GlobalValues.userTelefono
    val idUsuario = GlobalValues.userId

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: DetallesImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_productos)

        val campoTelefono: TextView = findViewById(R.id.telefonoDetalle)
        campoTelefono.text = Telefono

        val tvNombreUsuario: TextView = findViewById(R.id.nombreDetalle)
        tvNombreUsuario.text = Name+" "+Apellido

        val regresar: ImageView = findViewById(R.id.flechaprincpal)
        regresar.setOnClickListener{
            val intent: Intent = Intent(this, principal::class.java)
            startActivity(intent)
            finish()
        }

        val venta = intent.getSerializableExtra("venta") as? Venta
        if (venta != null) {
            // Usa el objeto venta como necesites
            // Por ejemplo:

            val textViewTitulo: TextView = findViewById(R.id.TituloDetalle)
            textViewTitulo.text = venta.titulo +","+ " Estado: "+ venta.estado

            val textViewPrecio: TextView = findViewById(R.id.precioDetalle)
            textViewPrecio.text = "$: ${venta.precio}"+ "   "+venta.ubicacion

            val textViewDescripcion: TextView = findViewById(R.id.descripcionDetalle)
            textViewDescripcion.text = "${venta.descripcion}"

            // Y así con los demás campos
            val imageModels = venta.imagenes.map { ImageModel(Uri.parse(it)) }
            recyclerView = findViewById(R.id.recyclerViewDetalles)
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            imageAdapter = DetallesImageAdapter(imageModels) { imageUri ->
                showImageFullScreen(imageUri)
            }
            recyclerView.adapter = imageAdapter

            //mensaje


            val myButton = findViewById<Button>(R.id.btnEnviarDetalles)
            myButton.setOnClickListener {
                // Código para manejar el evento de clic del botón aquí
                Toast.makeText(this, "Botón clickeado", Toast.LENGTH_SHORT).show()
                val phoneNumber = "+593 $Telefono"
                val message = "Hola, me interesa el artículo. ¿Ayudame con mas informacion?"
                openWhatsAppChat(this, phoneNumber, message)
            }
        }

    }

    private fun showImageFullScreen(imageUrl: String) {
        val dialog = FullScreenImageDialog.newInstance(imageUrl)
        dialog.show(supportFragmentManager, "FullScreenImageDialog")
    }

    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String) {
        val uri = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        context.startActivity(intent)
    }

}