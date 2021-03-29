package net.inqer.touringapp.data.local.dao

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery


interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReadId(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<T>?)

    @Update
    suspend fun update(entity: T)

    @Update
    suspend fun updateAll(list: List<T>?)

    suspend fun getAll(): List<T> {
        val query = SimpleSQLiteQuery(
                "SELECT * FROM $tableName"
        )
        return doGetAll(query)
    }

    @RawQuery
    fun doGetAll(query: SupportSQLiteQuery): List<T>

    val tableName: String

    @Delete
    suspend fun delete(entity: T)

    @Delete
    suspend fun deleteAll(list: List<T>?)
}