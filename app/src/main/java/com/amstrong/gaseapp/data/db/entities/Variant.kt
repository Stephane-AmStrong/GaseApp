package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Variant(
        val variant_id: String,
        val item_id: String,
        var item_name: String = "",
        var variant_name: String = "",
        var image_url: String?,
        val category_id: String?,
        val sku: String?,
        val reference_variant_id: String?,
        val option1_value: String?,
        val option2_value: String?,
        val option3_value: String?,
        val barcode: String?,
        var in_stock: Float = 0F,
        val cost: Float = 0F,
        val purchase_cost: Float = 0F,
        val default_pricing_type: String,
        val default_price: Float = 0F,
        val stores: List<VariantStore>,
        val created_at: Date?,
        val updated_at: Date?,
        val deleted_at: Date?,

        val workstation_id: String?,
//        val Item: Item?,



        val modifiers_ids: List<String>,
//        val stores: List<Store>,
        val tax_ids: List<String>,

        var color: String = "GREY",

        )