package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Category
import com.amstrong.gaseapp.databinding.RowCategoryBinding
import com.amstrong.gaseapp.utils.visible
import com.xwray.groupie.databinding.BindableItem


class CategoryRow(
        private val _category: Category
) : BindableItem<RowCategoryBinding>() {

    override fun getLayout() = R.layout.row_category

    val category: Category
        get() = _category

    override fun bind(viewBinding: RowCategoryBinding, position: Int) {
        viewBinding.txtCategoryName.text = _category.name
        viewBinding.txtWorkstationName.text = _category.workstation?.name
        viewBinding.imgAvailable.visible(category.available_for_sale)
    }
}