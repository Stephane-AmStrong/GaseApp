package com.amstrong.gaseapp.data.db.entities.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.amstrong.gaseapp.data.db.entities.LineDiscount
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.LineModifier
import com.amstrong.gaseapp.data.db.entities.LineTax

data class LineItemWithTaxes (
    @Embedded
    val lineItem: LineItem,
    @Relation(
            parentColumn = "LineItem_number",
            entityColumn = "LineItem_number_fk",
//            entity = LineTax::class
    )
    val lineTaxes: MutableList<LineTax>,
//    val lineDiscounts: List<LineDiscount>,
//    val lineModifiers: List<LineModifier>,

){
    @Ignore
    var qte: Int = 0

}

