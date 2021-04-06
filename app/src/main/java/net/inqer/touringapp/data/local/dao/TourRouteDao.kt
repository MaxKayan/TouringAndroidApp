package net.inqer.touringapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.data.models.response.TourRouteBrief

@Dao
interface TourRouteDao : BaseDao<TourRoute> {

    @Update(entity = TourRoute::class)
    suspend fun updateByBriefList(list: List<TourRouteBrief>)

    @Insert(entity = TourRoute::class)
    suspend fun createByBriefList(list: List<TourRouteBrief>)

    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getRoute(id: Long): TourRoute

    @Query("SELECT * FROM routes")
    fun getRoutesFlow(): Flow<List<TourRoute>>

    @Query("SELECT * FROM routes")
    suspend fun getRoutesList(): List<TourRoute>

    @Query("UPDATE routes SET isActive = CASE id WHEN :routeId THEN 1 ELSE 0 END")
    suspend fun setActiveRoute(routeId: Long)
}