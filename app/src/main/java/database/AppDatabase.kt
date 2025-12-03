package database

import DAO.UsuarioDao
import DAO.VentaDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import entidades.Converters
import entidades.Usuario
import entidades.Venta


@Database(entities = [Usuario::class, Venta::class], version = 2)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "marketplace"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}