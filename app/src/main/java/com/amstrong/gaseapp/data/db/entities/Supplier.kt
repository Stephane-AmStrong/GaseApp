package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Supplier(
        val id: String,
        val name: String,
        val contact: String,
        val email: String,
        val phone_number: String,
        val website: String,
        val address_1: String,
        val address_2: String,
        val city: String,
        val region: String,
        val postal_code: String,
        val country_code: String,
        val note: String,
        val created_at: Date?,
        val updated_at: Date?,
        val deleted_at: Date?
)