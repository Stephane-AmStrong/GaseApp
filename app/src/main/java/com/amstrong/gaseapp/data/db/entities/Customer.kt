package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Customer(
        val id: String,
        val name: String,
        val email: String?,
        val phone_number: String,
        val address: String,
        val city: String,
        val region: String?,
        val postal_code: String,
        val country_code: String,
        val customer_code: String,
        val note: String?,
        val first_visit: String?,
        val last_visit: String?,
        val total_visits: Long?,
        val total_spent: Float?,
        val total_points: Long?,
        val created_at: Date?,
        val updated_at: Date?
)