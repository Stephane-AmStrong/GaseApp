package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.OrderLine
import com.amstrong.gaseapp.databinding.RowLineItemBinding
import com.amstrong.gaseapp.databinding.RowOrderLineBinding
import com.amstrong.gaseapp.utils.sumByFloat
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.xwray.groupie.databinding.BindableItem


class OrderLineRow (
    private val _orderLine: OrderLine
) : BindableItem<RowOrderLineBinding>(){

    override fun getLayout() = R.layout.row_order_line
    val orderLine: OrderLine
        get() = _orderLine

    override fun bind(viewBinding: RowOrderLineBinding, position: Int) {
        viewBinding.txtName.text = _orderLine.variant.item_name
        viewBinding.txtQte.text = viewBinding.txtQte.context.getString(R.string.pattern_qte_ordered,_orderLine.amount.toDecimalFormat())
        //viewBinding.txtAmount.text = (_orderLine.amount).toDecimalFormat()

        viewBinding.progressbarReceived.min = 0
        viewBinding.progressbarReceived.max = _orderLine.quantity_order.toInt()
        viewBinding.progressbarReceived.progress = _orderLine.quantity_received.toInt()

    }
}