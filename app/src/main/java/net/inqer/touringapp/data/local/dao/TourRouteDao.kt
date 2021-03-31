package net.inqer.touringapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.inqer.touringapp.data.models.TourRoute

@Dao
interface TourRouteDao : BaseDao<TourRoute> {
    @Query("SELECT * FROM routes WHERE id = :id")
    suspend fun getRoute(id: Long): TourRoute

    @Query("SELECT * FROM routes")
    fun getRoutesFlow(): Flow<List<TourRoute>>
}