package entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable


@Entity(tableName = "ventas",
    foreignKeys = [ForeignKey(
        entity = Usuario::class,
        parentColumns = ["id"],
        childColumns = ["id_usuario_per"],
        onDelete = ForeignKey.CASCADE
    )])
@TypeConverters(Converters::class)
data class Venta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "imagenes") val imagenes: List<String>,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "precio") val precio: Double,
    @ColumnInfo(name = "categoria") val categoria: String,
    @ColumnInfo(name = "ubicacion") val ubicacion: String,
    @ColumnInfo(name = "estado") val estado: String,
    @ColumnInfo(name = "descripcion") val descripcion: String,
    @ColumnInfo(name = "id_usuario_per") val idUsuarioPer: Int
): Serializable
