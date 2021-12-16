package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.databinding.RowWorkstationBinding
import com.xwray.groupie.databinding.BindableItem


class WorkstationRow(
        private val _workstation: Workstation
) : BindableItem<RowWorkstationBinding>() {

    override fun getLayout() = R.layout.row_workstation

    val workstation: Workstation
        get() = _workstation

    override fun bind(viewBinding: RowWorkstationBinding, position: Int) {
//        Glide.with(viewBinding.imgUser)
//            .load(_variant.image_url)
//                .apply(RequestOptions()
//                        .placeholder(R.drawable.ic_dish)
//                )
//            .into(viewBinding.imgMenu)

        viewBinding.txtName.text = _workstation.name
    }
}