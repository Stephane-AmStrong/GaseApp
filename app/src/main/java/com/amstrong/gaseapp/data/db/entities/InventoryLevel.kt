package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

//@Entity
data class InventoryLevel(
        val id: String?,
//        @PrimaryKey(autoGenerate = false)
    val variant_id: String,
        val store_id: String,
        var in_stock: Float?,
        val stock_after: Float?,
        val updated_at: Date?,
        val pushed: Boolean,
) {
//    @Ignore
    var variant: Variant? = null
//    @Ignore
    var variantstore: VariantStore? = null


    constructor(
        id: String?,
        variant_id: String,
        store_id: String,
        in_stock: Float?,
        stock_after: Float?,
        updated_at: Date?,
        pushed: Boolean,
        variant: Variant?,
        variantstore: VariantStore?,
    ) : this(
        id,
        variant_id,
        store_id,
        in_stock,
        stock_after,
        updated_at,
        pushed,
    ) {
        this.variant = variant
        this.variantstore = variantstore
    }
}