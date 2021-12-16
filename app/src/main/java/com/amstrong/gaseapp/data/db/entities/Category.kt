package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Category(
        val id: String,
        val name: String,
        val color: String,
        val created_at: Date,
        val deleted_at: Date,
        var workstation_id: String?,
        val workstation: Workstation?,
        var available_for_sale: Boolean,
)
