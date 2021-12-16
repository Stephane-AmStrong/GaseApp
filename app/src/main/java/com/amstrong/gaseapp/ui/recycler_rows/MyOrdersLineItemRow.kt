package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.databinding.RowMyOrdersLineItemBinding
import com.xwray.groupie.databinding.BindableItem
import java.time.LocalDate


class MyOrdersLineItemRow (
    private val _lineItem: LineItem
) : BindableItem<RowMyOrdersLineItemBinding>(){

    override fun getLayout() = R.layout.row_my_orders_line_item
    val lineItem: LineItem
        get() = _lineItem

    override fun bind(viewBinding: RowMyOrdersLineItemBinding, position: Int) {
        viewBinding.txtName.text = _lineItem.item_name
        viewBinding.txtTime.text = LocalDate.now().toString()
//        val dec = DecimalFormat("#,###.##")
//        viewBinding.txtAmount.text = NumberFormat.getInstance(Locale.FRENCH).format(lineItem.price*lineItem.quantity)
        if (_lineItem.caterer_id==null){
            viewBinding.txtReady.text = viewBinding.txtReady.context.resources.getString(R.string.dish_pending)
            viewBinding.txtReady.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_av_timer_16, 0)
        }else{
            viewBinding.txtReady.text = viewBinding.txtReady.context.resources.getString(R.string.dish_ready)
            viewBinding.txtReady.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_outline_16, 0)

            if (_lineItem.served_at!=null){
                viewBinding.txtReady.text = viewBinding.txtReady.context.resources.getString(R.string.dish_served)
                viewBinding.txtReady.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_check_circle_16, 0)
            }
        }
    }
}