package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.databinding.RowItemOrderedBinding
import com.amstrong.gaseapp.utils.formatToPattern
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.databinding.BindableItem
import java.util.*


class ItemOrderedRow(
        private val _lineItem: LineItem
) : BindableItem<RowItemOrderedBinding>(){

    override fun getLayout() = R.layout.row_item_ordered

    val lineItem: LineItem
        get() = _lineItem

    var isPause : Boolean = true

    override fun bind(viewBinding: RowItemOrderedBinding, position: Int) {
        //Log.d(ContentValues.TAG, "ItemOrderedRow: placeholder color "+ _lineItem.Variant?.color)
        Glide.with(viewBinding.imgItem)
            .load(_lineItem.image_url)
                .apply(RequestOptions()
                        .placeholder(R.drawable.ic_dish)
                        //.placeholder(ColorDrawable(_lineItem.Variant?.color?.toColorInt()))
                )
            .into(viewBinding.imgItem)

        viewBinding.imgItem.contentDescription = _lineItem.item_name
        viewBinding.txtItemName.text = _lineItem.item_name
        val elapsedTimeInMillisecondes =Date().toInstant().toEpochMilli() - _lineItem.asked_at?.toInstant()?.toEpochMilli()!!

        /*
        val day: Long = elapsedTimeInMillisecondes / (1000 * 60 * 60 * 24)
        val hour: Long = elapsedTimeInMillisecondes / (1000 * 60 * 60) - day * 24
        val min: Long = elapsedTimeInMillisecondes / (60 * 1000) - day * 24 * 60 - hour * 60
        val s: Long = elapsedTimeInMillisecondes / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
        */

        if (elapsedTimeInMillisecondes<86400000){

            val day: Long = elapsedTimeInMillisecondes / (1000 * 60 * 60 * 24)
            val hour: Long = elapsedTimeInMillisecondes / (1000 * 60 * 60) - day * 24
            val min: Long = elapsedTimeInMillisecondes / (60 * 1000) - day * 24 * 60 - hour * 60
            val s: Long = elapsedTimeInMillisecondes / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60
            //

            if (hour<=0){
                viewBinding.txtElapsedTime.text = ""+min.toDecimalFormat()+" : "+s
            }else{
                viewBinding.txtElapsedTime.text = ""+hour.toDecimalFormat()+" h : "+min.toDecimalFormat()+" : "+s
            }


        }else{
            viewBinding.txtElapsedTime.text = _lineItem.asked_at?.formatToPattern("d MMM yyyy")
        }
        viewBinding.txtReceiptName.text = _lineItem.Receipt?.order
    }
}