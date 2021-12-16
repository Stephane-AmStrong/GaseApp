package com.amstrong.gaseapp.data.db.entities

data class TotalTax(
    val tax_id: String,
    val receipt_number: String,
    val receipt_id: String,
    val receipt_tax_number: String,
    val type: String,
    val name: String,
    val rate: Float,
    val money_amount: Float,

    val pushed: Boolean,

    val Tax: Tax,
    val Receipt: Receipt,
)