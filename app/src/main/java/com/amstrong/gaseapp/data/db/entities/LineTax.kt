package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class LineTax(
        val line_id: String?,
        var id: String,
) {
    var LineItem_number_fk: Long = 0
    var money_amount: Float? = null
    var type: String = ""
    var name: String = ""
    var rate: Float? = 0f

    var pushed: Boolean = false

    @PrimaryKey(autoGenerate = true)
    var tax_number: Long = 0

    @Ignore
    var LineItem: LineItem? = null

    @Ignore
    var Tax: Tax? = null

    constructor(
            line_id: String?,
            id: String,
            LineItem_number_fk: Long = 0,
            money_amount: Float?,
            type: String,
            name: String,
            rate: Float?,
            pushed: Boolean,
            LineItem: LineItem?,
            Tax: Tax?,
    ) : this(
            line_id,
            id
    ) {
        this.LineItem_number_fk = LineItem_number_fk
        this.money_amount = money_amount
        this.type = type
        this.name = name
        this.rate = rate
        this.pushed = pushed
        this.LineItem = LineItem
        this.Tax = Tax
    }
}