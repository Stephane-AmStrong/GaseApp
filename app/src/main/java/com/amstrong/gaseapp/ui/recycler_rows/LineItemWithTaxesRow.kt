package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.databinding.RowLineItemBinding
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.xwray.groupie.databinding.BindableItem


class LineItemWithTaxesRow (
    private val _lineItemWithTaxes: LineItemWithTaxes
) : BindableItem<RowLineItemBinding>(){

    override fun getLayout() = R.layout.row_line_item
    val lineItemWithTaxes: LineItemWithTaxes
        get() = _lineItemWithTaxes

    override fun bind(viewBinding: RowLineItemBinding, position: Int) {
        viewBinding.txtName.text = _lineItemWithTaxes.lineItem.item_name
        viewBinding.txtQte.text = "x ${_lineItemWithTaxes.qte.toDecimalFormat()}"
//        val dec = DecimalFormat("#,###.##")
//        viewBinding.txtAmount.text = NumberFormat.getInstance(Locale.FRENCH).format(lineItem.price*lineItem.quantity)
        viewBinding.txtAmount.text = (_lineItemWithTaxes.lineItem.price*_lineItemWithTaxes.lineItem.quantity).toDecimalFormat()
    }
}