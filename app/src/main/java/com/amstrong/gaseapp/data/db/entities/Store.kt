package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val phone_number: String,
    val description: String,
    val created_at: Date,
    val updated_at: Date,
    val deleted_at: Date
)