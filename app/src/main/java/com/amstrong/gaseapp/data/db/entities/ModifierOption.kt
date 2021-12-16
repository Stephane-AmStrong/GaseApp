package com.amstrong.gaseapp.data.db.entities

data class ModifierOption(
        val id: String,
        val name: String,
        val position: Int,
        val modifier_id: String?,

        val Modifier: Modifier?,
)