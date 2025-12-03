package DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import entidades.Usuario

@Dao
interface UsuarioDao {
    @Insert
     fun insertarUsuario(usuario: Usuario)

    @Update
    fun actualizarUsuario(usuario: Usuario)

    @Delete
    fun eliminarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena")
    fun validarLogin(correo: String, contrasena: String): Usuario?


}