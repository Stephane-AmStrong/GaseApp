package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Modifier(
        val id: String,
        val name: String,
        val position: Int,
        val stores: List<String>,
        val modifier_options: List<ModifierOption>,
        val created_at: Date,
        val updated_at: Date?,
        val deleted_at: Date?
)