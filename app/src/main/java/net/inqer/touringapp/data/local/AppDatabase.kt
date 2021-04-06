package net.inqer.touringapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.inqer.touringapp.data.converters.MainTypeConverters
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute

@Database(
        entities = [TourRoute::class],
        version = 3,
        exportSchema = false
)
@TypeConverters(MainTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tourRouteDao(): TourRouteDao
}