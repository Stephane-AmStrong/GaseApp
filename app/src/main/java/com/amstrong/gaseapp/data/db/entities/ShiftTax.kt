package com.amstrong.gaseapp.data.db.entities

data class ShiftTax(
        val shift_id: String,
        val tax_id: String,
        val money_amount: Long,
        val pushed: Boolean,

        val Shift: Shift?,
        val Tax: Tax?,
)