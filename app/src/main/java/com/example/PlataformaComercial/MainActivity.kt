package com.example.trabajomarketplace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import database.AppDatabase
import entidades.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextContraseña: EditText
    private lateinit var btnIngresar: Button
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Asegurarse de que la actividad no esté en modo de pantalla completa
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusBar)


        //redireccion para registro
        val registro: TextView = findViewById(R.id.registerTextView)
        registro.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        db = AppDatabase.getDatabase(this)

        editTextCorreo = findViewById(R.id.emailEditText)
        editTextContraseña = findViewById(R.id.passwordEditText)
        btnIngresar = findViewById(R.id.btnIngresar)

        btnIngresar.setOnClickListener {
            val correo = editTextCorreo.text.toString()
            val contraseña = editTextContraseña.text.toString()

            if (correo.isNotEmpty() && contraseña.isNotEmpty()) {
                mostrarProgressDialog() // Mostrar el diálogo antes de iniciar la tarea de inicio de sesión

                GlobalScope.launch(Dispatchers.IO) {
                    validarLogin(correo, contraseña)
                }
            } else {
                mostrarMensajeError("Por favor, ingresa tu correo y contraseña")
            }
        }
    }

    private fun mostrarProgressDialog() {
        Log.d("MainActivity", "Mostrando diálogo de progreso")
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Iniciando sesión...")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }


    private fun validarLogin(correo: String, contraseña: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val usuario = db.usuarioDao().validarLogin(correo, contraseña)
            if (usuario != null) {
                // Las credenciales son válidas, iniciar sesión y mostrar pantalla principal
                withContext(Dispatchers.Main) {
                    kotlinx.coroutines.delay(2000)
                    progressDialog.dismiss()
                    iniciarSesionExitoso(usuario)
                }
            } else {
                // Las credenciales son inválidas, mostrar mensaje de error
                withContext(Dispatchers.Main) {
                    kotlinx.coroutines.delay(2000)
                    progressDialog.dismiss()
                    mostrarMensajeError("Correo o contraseña incorrectos")
                }
            }
        }
    }

    private fun iniciarSesionExitoso(usuario: Usuario) {
        GlobalValues.userId = usuario.id
        GlobalValues.userName = usuario.nombre
        GlobalValues.userApellido = usuario.apellido
        GlobalValues.userTelefono = usuario.celular
        GlobalValues.userCorreo = usuario.correo
        GlobalValues.userContrasena = usuario.contrasena
        val intent = Intent(this, principal::class.java)
        startActivity(intent)
        finish() // Finalizar esta actividad para que no se pueda volver atrás desde la pantalla principal
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}

object GlobalValues {
    var userId: Int = 0
    var userName: String = ""
    var userApellido: String = ""
    var userTelefono: String = ""
    var userCorreo: String = ""
    var userContrasena: String = ""
}