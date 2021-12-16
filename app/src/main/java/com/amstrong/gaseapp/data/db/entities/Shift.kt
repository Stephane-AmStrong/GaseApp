package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Shift(
        val id: String?,
        val store_id: String,
        val pos_device_id: String,
        val opened_at: Date,
        val closed_at: Date?,
        val employee_id: String,
        val opened_by_employee: String,
        val closed_by_employee: String,
        val starting_cash: Float,
        val cash_payments: Float,
        val cash_refunds: Float,
        val paid_in: Float,
        val paid_out: Float,
        val expected_cash: Float,
        val actual_cash: Float,
        val gross_sales: Float,
        val refunds: Float,
        val discounts: Float,
        val net_sales: Float,
        val tip: Float?,
        val surcharge: Float?,
        val taxes: List<ShiftTax>,
        val payments: List<Payment>,
        val cash_movements: List<CashMovement>,

        val pushed: Boolean,
        val VariantStore: VariantStore,
        val PosDevice: POSDevice,
        val Cashier: Employee,
)