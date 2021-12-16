package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Item(
        val id: String,
        val handle: String?,
        val item_name: String,
        val reference_id: String?,
        val category_id: String?,
        val track_stock: Boolean,
        val sold_by_weight: Boolean,
        val is_composite: Boolean,
        val use_production: Boolean,
        val components: List<Component>,
        val primary_supplier_id: String?,
        val tax_ids: List<String>,
        val modifiers_ids: List<String>,
        val form: String?,
        val color: String,
        val image_url: String,
        val option1_name: String?,
        val option2_name: String?,
        val option3_name: String?,
        val created_at: Date,
        val updated_at: Date?,
        val deleted_at: Date?,
        val variants: List<Variant>,

//        val Category: Category?,
)