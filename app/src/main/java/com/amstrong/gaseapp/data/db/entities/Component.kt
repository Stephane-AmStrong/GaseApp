package com.amstrong.gaseapp.data.db.entities

data class Component(
        val variant_id: String,
        val item_id: String,
        val component_number: Int,
        val quantity: Float,

        val Variant: Variant?,
        val Item: Item?
)