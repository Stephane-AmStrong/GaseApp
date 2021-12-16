package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class POSDevice(
        val id: String,
        val store_id: String,
        val name: String,
        val activated: Boolean,
        val deleted_at: Date?,
        val VariantStore: VariantStore?
)