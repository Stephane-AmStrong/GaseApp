package com.amstrong.gaseapp.ui.recycler_rows

import android.graphics.drawable.Drawable
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Order
import com.amstrong.gaseapp.databinding.RowOrderBinding
import com.amstrong.gaseapp.utils.formatToPattern
import com.amstrong.gaseapp.utils.sumByFloat
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.xwray.groupie.databinding.BindableItem
import java.lang.String


class OrderRow(
        private val _order: Order
) : BindableItem<RowOrderBinding>() {

    override fun getLayout() = R.layout.row_order

    val order: Order
        get() = _order

    override fun bind(viewBinding: RowOrderBinding, position: Int) {
        viewBinding.txtReference.text = _order.reference
        viewBinding.txtStore.text = _order.store?.name
        viewBinding.txtDates.text = viewBinding.cardMenu.context.getString(R.string.pattern_dates, _order.purchase_date.formatToPattern("d MMM yyyy"), _order.expected_date?.formatToPattern("d MMM yyyy"))
        viewBinding.progressbarReceived.min = 0
        viewBinding.progressbarReceived.max = _order.order_lines.sumByFloat { it.quantity_order }.toInt()
        viewBinding.progressbarReceived.progress = _order.order_lines.sumByFloat { it.quantity_received }.toInt()
        viewBinding.txtRatioReceived.text = viewBinding.cardMenu.context.getString(R.string.pattern_received_ratio, _order.order_lines.sumByFloat { it.quantity_received }.toDecimalFormat(), _order.order_lines.sumByFloat { it.quantity_order }.toDecimalFormat())



    }
}