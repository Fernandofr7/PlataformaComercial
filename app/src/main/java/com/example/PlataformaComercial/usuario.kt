package com.example.trabajomarketplace

import DAO.UsuarioDao
import adapters.ProductoAdapter
import adapters.editarProductosAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import database.AppDatabase
import entidades.Usuario
import entidades.Venta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class usuario : AppCompatActivity() {
    val Name = GlobalValues.userName
    val Apellido = GlobalValues.userApellido
    val Telefono = GlobalValues.userTelefono
    val idUsuario = GlobalValues.userId
    val correoUser = GlobalValues.userCorreo
    val contraUser = GlobalValues.userContrasena

    //private lateinit var usuario: Usuario
    private lateinit var db: AppDatabase
    private lateinit var userIcon: ImageView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var btnEnviar: Button

    private lateinit var recyclerViewVentas: RecyclerView
    private lateinit var ventasAdapter: editarProductosAdapter
    //rivate lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar)

        db = AppDatabase.getDatabase(this)

        userIcon = findViewById(R.id.userIcon)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextCelular = findViewById(R.id.editTextCelular)
        btnEnviar = findViewById(R.id.btnEnviar)

        userIcon.setOnClickListener {
            toggleVisibility()
        }

        //usuario = intent.getSerializableExtra("usuario") as? Usuario ?: return

        btnEnviar.setOnClickListener {
            val nuevoNombre = editTextNombre.text.toString().trim()
            val nuevoApellido = editTextApellido.text.toString().trim()
            val nuevoTelefono = editTextCelular.text.toString().trim()

            if (validarCampos(nuevoNombre, nuevoApellido, nuevoTelefono)) {
                actualizarUsuario(idUsuario, nuevoNombre, nuevoApellido, nuevoTelefono)
            }
        }

        val tvNombreUsuario: TextView = findViewById(R.id.textObtenerNombre)
        tvNombreUsuario.text = Name+" "+Apellido
        val campoNombre: TextView = findViewById(R.id.editTextNombre)
        campoNombre.text = Name
        val campoApellido: TextView = findViewById(R.id.editTextApellido)
        campoApellido.text = Apellido
        val campoTelefono: TextView = findViewById(R.id.editTextCelular)
        campoTelefono.text = Telefono

        val regresar: ImageView = findViewById(R.id.flechaprincpal)
        regresar.setOnClickListener{
            val intent: Intent = Intent(this, principal::class.java)
            startActivity(intent)
            finish()
        }

        val cerrarSesionTextView: TextView = findViewById(R.id.cerrarSesion)
        cerrarSesionTextView.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        // Configurar RecyclerView y cargar productos del usuario
        recyclerViewVentas = findViewById(R.id.recyclerViewProductos)
        recyclerViewVentas.layoutManager = LinearLayoutManager(this)
        cargarProductos(idUsuario)


    }

    private fun actualizarUsuario(id: Int, nuevoNombre: String, nuevoApellido: String, nuevoTelefono: String) {
        GlobalScope.launch(Dispatchers.IO) {
            if (esNombreValido(nuevoNombre) && esApellidoValido(nuevoApellido) && esTelefonoValido(nuevoTelefono)) {
                val usuarioActualizado = Usuario(id, nuevoNombre, nuevoApellido, correoUser, nuevoTelefono, contraUser)

                db.usuarioDao().actualizarUsuario(usuarioActualizado)

                withContext(Dispatchers.Main) {
                    mostrarMensajeExito("Usuario actualizado correctamente")
                    irAInicioSesion()
                }
            }else{
                withContext(Dispatchers.Main) {
                    mostrarMensajeError("Los datos ingresados no son válidos")
                    }
            }
        }
    }

    private fun irAInicioSesion() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun esNombreValido(nombre: String): Boolean {
        return nombre.matches(Regex("^[a-zA-Z]{3,}\$"))
    }

    private fun esApellidoValido(apellido: String): Boolean {
        return apellido.matches(Regex("^[a-zA-Z]{3,}\$"))
    }

    private fun esTelefonoValido(telefono: String): Boolean {
        return telefono.matches(Regex("^09\\d{8}\$"))
    }

    private fun validarCampos(nombre: String, apellido: String, telefono: String): Boolean {
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            mostrarMensajeError("Todos los campos son obligatorios")
            return false
        }
        return true
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarMensajeExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun toggleVisibility() {
        if (editTextNombre.visibility == View.GONE) {
            editTextNombre.visibility = View.VISIBLE
            editTextApellido.visibility = View.VISIBLE
            editTextCelular.visibility = View.VISIBLE
            btnEnviar.visibility = View.VISIBLE
        } else {
            editTextNombre.visibility = View.GONE
            editTextApellido.visibility = View.GONE
            editTextCelular.visibility = View.GONE
            btnEnviar.visibility = View.GONE
        }
    }

    private fun cargarProductos(idUsuario: Int) {
        val db = AppDatabase.getDatabase(this)
        val ventaDao = db.ventaDao()

        Thread {
            val ventas = ventaDao.obtenerVentasPorUsuario(idUsuario)
            runOnUiThread {
                ventasAdapter = editarProductosAdapter(this, ventas,
                    onEditClickListener = { venta -> onEditarVenta(venta) },
                    onDeleteClickListener = { venta -> onEliminarVenta(venta) }
                )
                recyclerViewVentas.adapter = ventasAdapter
            }
        }.start()
    }

    private fun onEditarVenta(venta: Venta) {
        val intent = Intent(this, ventas::class.java)
        intent.putExtra("VENTA", venta)
        intent.putExtra("EDITAR", true)
        intent.putExtra("VENTA_ID", venta.id)
        startActivity(intent)
        finish()
    }


    private fun onEliminarVenta(venta: Venta) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Publicación")
            .setMessage("¿Estás seguro de que quieres eliminar?")
            .setPositiveButton("Sí") { dialog, which ->
                // Ejecutar la eliminación en un hilo secundario utilizando coroutines
                CoroutineScope(Dispatchers.IO).launch{
                    val db = AppDatabase.getDatabase(this@usuario)
                    val ventaDao = db.ventaDao()
                    ventaDao.eliminarVenta(venta)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@usuario, "Venta eliminada", Toast.LENGTH_SHORT).show()
                        cargarProductos(GlobalValues.userId)
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { dialog, which ->
                cerrarSesion()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("mi_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}