package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.databinding.RowReceiptBinding
import com.amstrong.gaseapp.utils.formatToPattern
import com.amstrong.gaseapp.utils.sumByFloat
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.xwray.groupie.databinding.BindableItem


class ReceiptRow (
    private val _receipt: Receipt,
) : BindableItem<RowReceiptBinding>(){

    override fun getLayout() = R.layout.row_receipt

    val receipt: Receipt
        get() = _receipt

    override fun bind(viewBinding: RowReceiptBinding, position: Int) {
        viewBinding.txtReceiptName.text = _receipt.order
        viewBinding.txtWaiterName.text = _receipt.waiter_name
        viewBinding.txtReceiptTime.text = _receipt.receipt_date.formatToPattern()
        viewBinding.txtReceiptAmount.text = _receipt.line_items.sumByFloat { it.price }.toDecimalFormat()
    }
}