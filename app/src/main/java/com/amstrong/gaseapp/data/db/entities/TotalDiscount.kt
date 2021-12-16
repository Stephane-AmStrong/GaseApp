package com.amstrong.gaseapp.data.db.entities

import com.amstrong.gaseapp.data.db.entities.Discount

data class TotalDiscount(
        val discount_id: String,
        val receipt_number: String,
        val receipt_id: String,
        val receipt_discount_number: String,
        val type: String,
        val name: String,
        val percentage: Int,
        val money_amount: Float,

        val pushed: Boolean,

        val Discount: Discount?,
        val Receipt: Receipt?,
)