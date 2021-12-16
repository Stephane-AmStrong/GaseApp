package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Tax(
    val id: String,
    val type: String,
    val name: String,
    val rate: Float?,
    val stores: List<String>,
    val created_at: Date,
    val updated_at: Date,
    val deleted_at: Date
)