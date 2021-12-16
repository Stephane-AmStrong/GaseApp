package com.amstrong.mvvmsampleproject.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amstrong.gaseapp.data.db.entities.DateConverter
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.LineTax
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes

@Database(
    entities = [LineItem::class, LineTax::class],
    version = 1
)

@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getLineItemDao(): LineItemDao
//    abstract fun getLineTaxDao(): LineTax
//    abstract fun getInventoryLevelDao(): InventoryLevelDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MyDatabase.db"
            ).build()
    }
}