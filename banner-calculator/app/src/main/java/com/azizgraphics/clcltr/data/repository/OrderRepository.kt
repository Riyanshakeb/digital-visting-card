package com.azizgraphics.clcltr.data.repository

import androidx.lifecycle.LiveData
import com.azizgraphics.clcltr.data.db.OrderDao
import com.azizgraphics.clcltr.data.model.Order

class OrderRepository(private val orderDao: OrderDao) {
    val allOrders: LiveData<List<Order>> = orderDao.getAllOrders()

    suspend fun insert(order: Order): Long = orderDao.insert(order)

    suspend fun delete(order: Order) = orderDao.delete(order)
}
