package com.azizgraphics.clcltr.data.repository

import androidx.lifecycle.LiveData
import com.azizgraphics.clcltr.data.db.OrderDao
import com.azizgraphics.clcltr.data.model.Order

class OrderRepository(private val dao: OrderDao) {
    val allOrders: LiveData<List<Order>> = dao.getAll()
    suspend fun insert(order: Order) = dao.insert(order)
    suspend fun delete(order: Order) = dao.delete(order)
}
