package com.amstrong.gaseapp.ui.recycler_rows

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.graphics.toColorInt
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Variant
import com.amstrong.gaseapp.databinding.RowVariantBinding
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.databinding.BindableItem


class VariantRow (
    private val _variant: Variant
) : BindableItem<RowVariantBinding>(){

    override fun getLayout() = R.layout.row_variant

    val variant: Variant
        get() = _variant

    override fun bind(viewBinding: RowVariantBinding, position: Int) {
        Glide.with(viewBinding.imgItem)
            .load(_variant.image_url)
                .apply(RequestOptions()
//                        .placeholder(R.drawable.ic_dish)

                        .placeholder(ColorDrawable(_variant.color.toColorInt()))
                        //.placeholder(ColorDrawable("GREY".toColorInt()))
                )
            .into(viewBinding.imgItem)



        viewBinding.imgItem.contentDescription = _variant.item_name
        viewBinding.txtItemName.text = _variant.item_name
        viewBinding.txtCount.text = _variant.in_stock.toDecimalFormat()
    }
}