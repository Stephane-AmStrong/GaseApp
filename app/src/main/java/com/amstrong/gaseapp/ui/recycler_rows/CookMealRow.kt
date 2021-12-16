package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Item
import com.amstrong.gaseapp.databinding.RowVariantBinding
import com.bumptech.glide.Glide
import com.xwray.groupie.databinding.BindableItem


class CookMealRow (
    private val _item: Item
) : BindableItem<RowVariantBinding>(){

    override fun getLayout() = R.layout.row_variant

    val clickedFood: Item
        get() = _item

    override fun bind(viewBinding: RowVariantBinding, position: Int) {
        Glide.with(viewBinding.imgItem)
            .load(_item.image_url)
            .into(viewBinding.imgItem)

        viewBinding.imgItem.contentDescription = _item.item_name
        viewBinding.txtItemName.text = _item.item_name
        viewBinding.txtCount.text = _item.item_name
    }
}