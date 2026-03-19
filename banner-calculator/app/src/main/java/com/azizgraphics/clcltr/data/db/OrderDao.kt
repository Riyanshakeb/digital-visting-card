package com.azizgraphics.clcltr.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.azizgraphics.clcltr.data.model.Order

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY date DESC")
    fun getAll(): LiveData<List<Order>>

    @Insert
    suspend fun insert(order: Order): Long

    @Delete
    suspend fun delete(order: Order)
}
