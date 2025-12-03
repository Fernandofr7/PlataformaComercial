package DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import entidades.Venta

@Dao
interface VentaDao {
    @Insert
     fun insertarVenta(venta: Venta)

    @Update
    fun actualizarVenta(venta: Venta)

    @Delete
     fun eliminarVenta(venta: Venta)

    @Query("SELECT * FROM ventas")
     fun obtenerVentas(): List<Venta>

    @Query("SELECT * FROM ventas WHERE categoria = :categoria")
     fun filtrarVentasPorCategoria(categoria: String): List<Venta>

    @Query("SELECT * FROM ventas WHERE id_usuario_per = :usuarioId")
    fun obtenerVentasPorUsuario(usuarioId: Int): List<Venta>
}