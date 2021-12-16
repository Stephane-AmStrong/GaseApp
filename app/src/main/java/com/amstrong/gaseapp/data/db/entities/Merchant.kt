package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Merchant(
        val id: String,
        val business_name: String,
        val email: String,
        val country: String,
        val created_at: Date
)