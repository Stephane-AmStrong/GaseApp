package com.amstrong.gaseapp.data.db.entities

data class PaymentDetails(
        val payment_detail_id: String?,
        val authorization_code: String?,
        val reference_id: String?,
        val entry_method: String,
        val card_company: String,
        val card_number: String,

        val pushed: Boolean
)