package com.amstrong.gaseapp.data.db.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*
import kotlin.collections.ArrayList

@Entity
data class LineItem(
        var receipt_id: String?,
        var id: String?,
        val item_id: String,
        val item_name: String?,
        val image_url: String?,
        val workstation_id: String?,
        val variant_id: String,
        val variant_name: String?,
        val quantity: Float = 0F,
        val price: Float = 0F,
        val cost: Float,
        val line_note: String?,
        var caterer_id: String?,
        var asked_at: Date?,
        var ready_at: Date?,
        var served_at: Date?,
        val excluded: Boolean,
        val pushed: Boolean,
) {

    //
    var sku: String? = null
    var gross_total_money: Float = 0F
    var total_money: Float = 0F
    var total_discount: Float = 0F
    var cost_total: Float = 0F

    var workstation_name: String? = null

    @PrimaryKey(autoGenerate = true)
    var LineItem_number: Long = 0

    @Ignore
    var line_taxes = mutableListOf<LineTax>()

    @Ignore
    var line_discounts: ArrayList<LineDiscount> = arrayListOf()

    @Ignore
    var line_modifiers: ArrayList<LineModifier> = arrayListOf()

    @Ignore
    var Item: Item? = null

    @Ignore
    var Variant: Variant? = null

    @Ignore
    var Receipt: Receipt? = null

    @Ignore
    var Workstation: Workstation? = null

    @Ignore
    var Caterer: Employee? = null

    constructor(
            receipt_id: String?,
            id: String?,
            item_id: String,
            item_name: String?,
            image_url: String?,
            workstation_id: String?,
            variant_id: String,
            variant_name: String?,
            quantity: Float = 0F,
            price: Float = 0F,
            cost: Float,
            line_note: String?,

            caterer_id: String?,

            asked_at: Date?,
            ready_at: Date?,
            served_at: Date?,

            excluded: Boolean,
            pushed: Boolean,
            line_taxes: ArrayList<LineTax>,
    ) : this(
            receipt_id,
            id,
            item_id,
            item_name,
            image_url,
            workstation_id,
            variant_id,
            variant_name,
            quantity,
            price,
            cost,
            line_note,
            caterer_id,
            asked_at,
            ready_at,
            served_at,
            excluded,
            pushed,
    ) {
        this.line_taxes = line_taxes
    }

    constructor(
            receipt_id: String?,
            id: String?,
            item_id: String,
            item_name: String?,
            image_url: String?,
            variant_id: String,
            variant_name: String?,
            sku: String,
            quantity: Float = 0F,
            price: Float = 0F,
            gross_total_money: Float = 0F,
            total_money: Float = 0F,
            cost: Float,
            cost_total: Float,
            line_note: String?,
            total_discount: Float,
            workstation_name: String?,
            workstation_id: String?,

            caterer_id: String?,
            asked_at: Date?,
            ready_at: Date?,
            served_at: Date?,

            line_taxes: ArrayList<LineTax>,

            line_discounts: ArrayList<LineDiscount>,
            line_modifiers: ArrayList<LineModifier>,
            Item: Item?,
            Variant: Variant?,
            Receipt: Receipt?,
            Workstation: Workstation?,
            Caterer: Employee?,

            excluded: Boolean,
            pushed: Boolean,
    ) : this(
            receipt_id,
            id,
            item_id,
            item_name,
            image_url,
            workstation_id,
            variant_id,
            variant_name,
            quantity,
            price,
            cost,
            line_note,
            caterer_id,

            asked_at,
            ready_at,
            served_at,
            excluded,
            pushed,
    ) {
        this.sku = sku
        this.gross_total_money = gross_total_money
        this.total_money = total_money
        this.cost_total = cost_total
        this.total_discount = total_discount

        this.workstation_name = workstation_name

        this.line_taxes = line_taxes
        this.line_discounts = line_discounts
        this.line_modifiers = line_modifiers
        this.Item = Item
        this.Variant = Variant
        this.Receipt = Receipt
        this.Workstation = Workstation
        this.Caterer = Caterer
    }

    constructor(
            lineItem: LineItem,
            lineTaxes: MutableList<LineTax>
    ) : this(
            lineItem.receipt_id,
            lineItem.id,
            lineItem.item_id,
            lineItem.item_name,
            lineItem.image_url,
            lineItem.workstation_id,
            lineItem.variant_id,
            lineItem.variant_name,
            lineItem.quantity,
            lineItem.price,
            lineItem.cost,
            lineItem.line_note,
            lineItem.caterer_id,

            lineItem.asked_at,
            lineItem.ready_at,
            lineItem.served_at,
            lineItem.excluded,
            lineItem.pushed,
    ) {
        this.sku = lineItem.sku
        this.gross_total_money = lineItem.gross_total_money
        this.total_money = lineItem.total_money
        this.cost_total = lineItem.cost_total
        this.total_discount = lineItem.total_discount

        this.workstation_name = lineItem.workstation_name

        this.line_taxes = lineTaxes
        this.line_discounts = lineItem.line_discounts
        this.line_modifiers = lineItem.line_modifiers
        this.Item = lineItem.Item
        this.Variant = lineItem.Variant
        this.Receipt = lineItem.Receipt
        this.Workstation = lineItem.Workstation
        this.Caterer = lineItem.Caterer
    }


    constructor(
            lineItem: LineItem,
            quantity: Float,
            lineTaxes: MutableList<LineTax>
    ) : this(
            lineItem.receipt_id,
            lineItem.id,
            lineItem.item_id,
            lineItem.item_name,
            lineItem.image_url,
            lineItem.workstation_id,
            lineItem.variant_id,
            lineItem.variant_name,
            quantity,
            lineItem.price,
            lineItem.cost,
            lineItem.line_note,
            lineItem.caterer_id,

            lineItem.asked_at,
            lineItem.ready_at,
            lineItem.served_at,
            lineItem.excluded,
            lineItem.pushed,
    ) {
        this.sku = lineItem.sku
        this.gross_total_money = lineItem.gross_total_money
        this.total_money = lineItem.total_money
        this.cost_total = lineItem.cost_total
        this.total_discount = lineItem.total_discount

        this.workstation_name = lineItem.workstation_name

        this.line_taxes = lineTaxes
        this.line_discounts = lineItem.line_discounts
        this.line_modifiers = lineItem.line_modifiers
        this.Item = lineItem.Item
        this.Variant = lineItem.Variant
        this.Receipt = lineItem.Receipt
        this.Workstation = lineItem.Workstation
        this.Caterer = lineItem.Caterer
    }

}