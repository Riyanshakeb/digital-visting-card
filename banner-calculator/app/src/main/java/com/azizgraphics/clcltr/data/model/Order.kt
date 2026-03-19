package com.azizgraphics.clcltr.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val totalAmount: Double = 0.0,
    val totalSqft: Double = 0.0,
    val totalQty: Int = 0,
    val currency: String = "PKR",
    val itemsJson: String = "[]"
)
