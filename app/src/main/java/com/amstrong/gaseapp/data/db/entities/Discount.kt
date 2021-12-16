package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Discount(
        val id: String,
        val type: String,
        val name: String,
        val discount_amount: Float?,
        val discount_percent: Float?,
        val stores: List<String>,
        val restricted_access: Boolean,
        val created_at: Date,
        val updated_at: Date?,
        val deleted_at: Date?
)