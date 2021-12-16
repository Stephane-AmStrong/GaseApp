package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class LineDiscount(
        val receipt_number: String,
        var line_number_fk: Long = 0,
        val discount_id: String,
        val line_discount_number: Int,
        val type: String?,
        val name: String?,
        val value: Int?,
        val money_amount: Float,

        val pushed: Boolean,

        @Ignore
        val LineItem: LineItem?,
        @Ignore
        val Discount: Discount?,

        @PrimaryKey(autoGenerate = true)
        var Discount_number: Long = 0,
)