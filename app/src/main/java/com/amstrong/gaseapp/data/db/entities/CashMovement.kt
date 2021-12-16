package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class CashMovement(
        val shift_id: String,
        val cashmovement_number: Long,
        val type: String,
        val money_amount: Long?,
        val comment: String?,
        val created_at: Date,
        val employee_id: String,
        val pushed: Boolean,

        val Shift: Shift?,
        val Employee: Employee?,
)