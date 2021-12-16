package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class PaymentType(
        val id: String,
        val name: String?,
        val type: String,
        val created_at: Date,
        val updated_at: Date?,
        val deleted_at: Date?
)