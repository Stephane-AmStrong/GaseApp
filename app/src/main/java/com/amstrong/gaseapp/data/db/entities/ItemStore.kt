package com.amstrong.gaseapp.data.db.entities

data class ItemStore(
    val availableForSale: Boolean,
    val lowStock: String?,
    val optimalStock: String?,
    val price: Int,
    val pricingType: String,
    val storeId: String
)