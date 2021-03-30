package net.inqer.touringapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.inqer.touringapp.data.converters.MainTypeConverters
import net.inqer.touringapp.data.local.dao.TourRouteBriefDao
import net.inqer.touringapp.data.local.dao.TourRouteDao
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.TourRouteBrief

@Database(
        entities = [TourRoute::class, TourRouteBrief::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(MainTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tourRouteDao(): TourRouteDao
    abstract fun tourRouteBriefDao(): TourRouteBriefDao
}