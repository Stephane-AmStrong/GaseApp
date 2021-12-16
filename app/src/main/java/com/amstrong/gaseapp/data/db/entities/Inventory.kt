package com.amstrong.gaseapp.data.db.entities

data class Inventory(
    val cursor: String?,
    val inventory_levels: List<InventoryLevel>
)