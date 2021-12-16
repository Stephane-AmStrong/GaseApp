package com.amstrong.gaseapp.data.db.entities

data class VariantStore(
        val variant_id: String,
        val store_id: String,
        val variant_store_number: String,
        val pricing_type: String,
        val price: Float=0f,
        val available_for_sale: Boolean,
        val optimal_stock: Float?,
        val low_stock: Float?,

//        val Variant: Variant?,
//        val Store: Store?,
)