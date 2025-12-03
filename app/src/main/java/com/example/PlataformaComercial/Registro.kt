package com.example.trabajomarketplace

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import database.AppDatabase
import entidades.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class Registro : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextCorreo: EditText
    private lateinit var editTextCelular: EditText
    private lateinit var editTextContraseña: EditText
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val regresar: ImageView = findViewById(R.id.flechalogin)
        regresar.setOnClickListener{
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        db = AppDatabase.getDatabase(this)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextCorreo = findViewById(R.id.editTextEmail)
        editTextCelular = findViewById(R.id.editTextCelular)
        editTextContraseña = findViewById(R.id.editTextPassword)
        btnRegistrar = findViewById(R.id.btnEnviar)

        btnRegistrar.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val apellido = editTextApellido.text.toString().trim()
            val correo = editTextCorreo.text.toString().trim()
            val celular = editTextCelular.text.toString().trim()
            val contraseña = editTextContraseña.text.toString()

            if (validarNombre(nombre) && validarApellido(apellido) && validarCorreo(correo) && validarCelular(celular) && validarContraseña(contraseña)) {
                insertarUsuario(nombre, apellido, correo, celular, contraseña)
                limpiarCampos()
            }
        }
    }

    private fun validarNombre(nombre: String): Boolean {
        return if (nombre.matches(Regex("[a-zA-Z]{3,}"))) {
            true
        } else {
            mostrarMensajeError("El nombre debe contener solo letras y tener más de 3 caracteres.")
            false
        }
    }

    private fun validarApellido(apellido: String): Boolean {
        return if (apellido.matches(Regex("[a-zA-Z]{3,}"))) {
            true
        } else {
            mostrarMensajeError("El apellido debe contener solo letras y tener más de 3 caracteres.")
            false
        }
    }

    private fun validarCorreo(correo: String): Boolean {
        val pattern = Pattern.compile("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
        return if (pattern.matcher(correo).matches()) {
            true
        } else {
            mostrarMensajeError("Ingrese un correo electrónico válido.")
            false
        }
    }

    private fun validarCelular(celular: String): Boolean {
        return if (celular.matches(Regex("09[0-9]{8}"))) {
            true
        } else {
            mostrarMensajeError("El número de celular debe empezar con '09' y contener 10 dígitos.")
            false
        }
    }

    private fun validarContraseña(contraseña: String): Boolean {
        return if (contraseña.length <= 10) {
            true
        } else {
            mostrarMensajeError("La contraseña no debe tener más de 10 caracteres.")
            false
        }
    }

    private fun insertarUsuario(nombre: String, apellido: String, correo: String, celular: String, contraseña: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val nuevoUsuario = Usuario(nombre = nombre, apellido = apellido, correo = correo, celular = celular, contrasena = contraseña)
            db.usuarioDao().insertarUsuario(nuevoUsuario)
            // Insertar el usuario en la base de datos en el contexto de fondo (IO)
            withContext(Dispatchers.Main) {
                mostrarMensajeExito("Usuario registrado correctamente")
            }
        }
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarMensajeExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun limpiarCampos() {
        editTextNombre.text.clear()
        editTextApellido.text.clear()
        editTextCorreo.text.clear()
        editTextCelular.text.clear()
        editTextContraseña.text.clear()
    }
}