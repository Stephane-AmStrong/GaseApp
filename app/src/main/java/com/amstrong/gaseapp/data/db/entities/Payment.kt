package com.amstrong.gaseapp.data.db.entities

import java.util.*


data class Payment(
        val payment_id: String?,
        val payment_type_id: String,
        val receipt_id: String?,
        val money_amount: Float,
        val pushed: Boolean?,
) {
    var _payment_detail_id: String? = null
    var _receipt_number: String? = null
    var _name: String? = null
    var _type: String? = null
    var _PaymentDetails: PaymentDetails? = null
    var _PaymentType: PaymentType? = null
    var _Receipt: Receipt? = null
    var _paid_at: Date? = null

    constructor(
            payment_id: String?,
            payment_type_id: String,
            payment_detail_id: String,
            receipt_number: String?,
            receipt_id: String?,
            name: String,
            type: String,
            money_amount: Float,
            paid_at: Date?,
            PaymentDetails: PaymentDetails?,
            PaymentType: PaymentType?,
            pushed: Boolean?,
            Receipt: Receipt?,
    ) : this(
            payment_id,
            payment_type_id,
            receipt_id,
            money_amount,
            pushed
    ) {
        _payment_detail_id = payment_detail_id
        _receipt_number = receipt_number
        _name = name
        _type = type
        _PaymentDetails = PaymentDetails
        _PaymentType = PaymentType
        _Receipt = Receipt
        _paid_at = paid_at

    }
}