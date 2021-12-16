package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

data class OrderLine(
        var id: String?,
        var order_id: String,
        var variant: Variant,
        var quantity_order: Float,
        var quantity_received: Float,
        var purchase_cost: Float,
        var amount: Float,
        val created_at: Date,
) {
    var order: Order? = null
    var variant_id: String = variant.variant_id
    var update_at: Date? = null
    var cancel_at: Date? = null

    constructor(
            id: String?,
            order_id: String,
            variant: Variant,
            quantity_order: Float,
            quantity_received: Float,
            purchase_cost: Float,
            amount: Float,
            created_at: Date,
            update_at: Date?,
            cancel_at: Date?,
    ) : this(
            id,
            order_id,
            variant,
            quantity_order,
            quantity_received,
            purchase_cost,
            amount,
            created_at,
    ) {
        this.update_at = update_at
        this.cancel_at = cancel_at
    }

    constructor(
            id: String?,
            order_id: String,
            variant_id: Variant,
            quantity_order: Float,
            quantity_received: Float,
            purchase_cost: Float,
            amount: Float,
            created_at: Date,
            update_at: Date?,
            cancel_at: Date?,
            order: Order?,
            variant: Variant,
    ) : this(
            id,
            order_id,
            variant_id,
            quantity_order,
            quantity_received,
            purchase_cost,
            amount,
            created_at,
            update_at,
            cancel_at,
    ) {
        this.order = order
        this.variant = variant
    }
}