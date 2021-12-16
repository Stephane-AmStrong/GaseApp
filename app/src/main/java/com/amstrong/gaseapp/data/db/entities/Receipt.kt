package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime;
import java.util.*
import kotlin.collections.ArrayList

data class Receipt(
        var note: String?,
        var order: String?,
        var line_items: ArrayList<LineItem>,
        var store_id: String,
        var created_at: Date,
        val receipt_date: Date,
        var isSelected: Boolean = false,
) {
    var dining_option: String? = null
    var id: String? = null
    //var id: String = "00000000-0000-0000-0000-000000000000"
//    var store_id: String = "f3c8b5b0-b8de-4ad4-b4b2-46bfa5288bf8"
//    var store_id: String = "faa633d3-711d-11ea-8d93-0603130a05b8"
    var receipt_number: String = ""
    var receipt_type: String = ""
    var employee_id: String? = null

    var refund_for: String? = null

    var total_money: Float = 0F
    var total_tax: Float = 0F

    //    var created_at: String = LocalDateTime.now().toString()
    var cancelled_at: Date? = null
    var source: String? = null
    var points_earned: Float = 0F
    var points_deducted: Int = 0
    var points_balance: Float = 0F
    var customer_id: String? = null
    var total_discount: Float? = null
//    var pos_device_id: String = "00000000-0000-0000-0000-000000000000"
    var pos_device_id: String? = null

    var total_discounts: ArrayList<TotalDiscount> = arrayListOf()
    var total_taxes: ArrayList<TotalTax> = arrayListOf()
    var tip: Float? = null

    var pushed: Boolean = false
    var waiter_name: String? = null
    var waiter_id: String? = null
    var cashier_id: String? = null
    var VariantStore: VariantStore? = null
    var PosDevice: POSDevice? = null
    var Waiter: Employee? = null
    var Cashier: Employee? = null
    var surcharge: Float? = null
    var payments: ArrayList<Payment> = arrayListOf()
    var closed_at: Date? = null

    constructor(
            note: String?,
            order: String?,
            dining_option: String?,
            line_items: ArrayList<LineItem>,
            store_id: String,
            created_at: Date,
            receipt_date: Date,
            isSelected: Boolean = false,
    ) : this (
            note,
            order,
            line_items,
            store_id,
            created_at,
            receipt_date,
            isSelected,
            ){
        this.dining_option = dining_option
    }

    constructor(
            id: String,
            receipt_number: String,
            note: String?,
            receipt_type: String,
            refund_for: String?,
            order: String?,
            created_at: Date,
            receipt_date: Date,
            isSelected: Boolean,
            cancelled_at: Date?,
            source: String?,
            total_money: Float,
            total_tax: Float,
            points_earned: Float,
            points_deducted: Int,
            points_balance: Float,
            customer_id: String,
            total_discount: Float?,
            employee_id: String,
            store_id: String,
            pos_device_id: String,
            dining_option: String,
            total_discounts: ArrayList<TotalDiscount>,
            total_taxes: ArrayList<TotalTax>,
            tip: Float?,
            surcharge: Float?,
            line_items: ArrayList<LineItem>,
            payments: ArrayList<Payment>,
            pushed: Boolean,
            waiter_name: String?,
            waiter_id: String?,
            cashier_id: String?,
            VariantStore: VariantStore?,
            PosDevice: POSDevice?,
            Waiter: Employee?,
            Cashier: Employee?,
    ) : this(
            note,
            order,
            line_items,
            store_id,
            created_at,
            receipt_date,
            isSelected,
    ) {
            this.id = id
            this.receipt_number = receipt_number
            this.receipt_type = receipt_type
            this.cancelled_at = cancelled_at
            this.source = source
            this.refund_for = refund_for
            this.created_at = created_at
            this.total_money = total_money
            this.total_tax = total_tax
            this.points_earned = points_earned
            this.points_deducted = points_deducted
            this.points_balance = points_balance
            this.customer_id = customer_id
            this.total_discount = total_discount
            this.pos_device_id = pos_device_id
            this.employee_id = employee_id
            this.total_discounts = total_discounts
            this.total_taxes = total_taxes
            this.tip = tip
            this.surcharge = surcharge
            this.payments = payments
            this.pushed = pushed
            this.waiter_id = waiter_id
            this.waiter_name = waiter_name
            this.cashier_id = cashier_id
            this.VariantStore = VariantStore
            this.PosDevice = PosDevice
            this.Waiter = Waiter
            this.Cashier = Cashier
    }
}