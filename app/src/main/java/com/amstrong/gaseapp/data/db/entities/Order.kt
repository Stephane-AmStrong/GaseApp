package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Order(
        var order_lines: ArrayList<OrderLine>,
) {
    var id: String? = null
    lateinit var reference: String
    var supplier_id: String? = null
    var store_id: String? = null
    var employee_id: String? = null
    var purchase_date: Date = Date()
    var expected_date: Date? = null
    var note: String? = null
    var amount: Float = 0f
    var status: String = "Draft"

    var supplier: Supplier? = null
    var store: Store? = null
    var employee: Employee? = null


    constructor(
            id: String?,
            reference: String,
            supplier_id: String?,
            store_id: String?,
            employee_id: String?,
            purchase_date: Date,
            expected_date: Date?,
            note: String?,
            amount: Float,
            status: String,
            order_lines: ArrayList<OrderLine>,
    ) : this(
            order_lines
    ) {
        this.id = id
        this.reference = reference
        this.supplier_id = supplier_id
        this.store_id = store_id
        this.employee_id = employee_id
        this.purchase_date = purchase_date
        this.expected_date = expected_date
        this.note = note
        this.amount = amount
        this.status = status
    }

    constructor(
            id: String?,
            reference: String,
            supplier: Supplier?,
            store: Store?,
            employee_id: String?,
            purchase_date: Date,
            expected_date: Date?,
            note: String?,
            amount: Float,
            status: String,
            order_lines: ArrayList<OrderLine>,
    ) : this(
            order_lines
    ) {
        this.id = id
        this.reference = reference
        this.supplier = supplier
        this.supplier_id = supplier?.id

        this.store = store
        this.store_id = store?.id
        this.employee_id = employee_id
        this.purchase_date = purchase_date
        this.expected_date = expected_date
        this.note = note
        this.amount = amount
        this.status = status
    }

    constructor(
            id: String?,
            reference: String,
            supplier_id: String,
            store_id: String,
            employee_id: String,
            purchase_date: Date,
            expected_date: Date?,
            note: String?,
            amount: Float,
            status: String,
            supplier: Supplier,
            store: Store,
            employee: Employee,
            order_lines: ArrayList<OrderLine>,
    ) : this(
            id,
            reference,
            supplier_id,
            store_id,
            employee_id,
            purchase_date,
            expected_date,
            note,
            amount,
            status,
            order_lines
    ) {
        this.supplier = supplier
        this.store = store
        this.employee = employee
    }
}