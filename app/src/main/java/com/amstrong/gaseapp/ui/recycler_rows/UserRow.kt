package com.amstrong.gaseapp.ui.recycler_rows

import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.databinding.RowUserBinding
import com.xwray.groupie.databinding.BindableItem


class UserRow(
        private val _user: Employee
) : BindableItem<RowUserBinding>() {

    override fun getLayout() = R.layout.row_user

    val user: Employee
        get() = _user

    override fun bind(viewBinding: RowUserBinding, position: Int) {
//        Glide.with(viewBinding.imgUser)
//            .load(_variant.image_url)
//                .apply(RequestOptions()
//                        .placeholder(R.drawable.ic_dish)
//                )
//            .into(viewBinding.imgMenu)

        viewBinding.txtName.text = _user.name
        viewBinding.txtWorkstation.text = _user.workstation.name
        if (_user.disabled_at == null) viewBinding.txtAccess.text = "" else viewBinding.txtAccess.text = viewBinding.txtAccess.context.resources.getString(R.string.account_disabled)
        viewBinding.txtWorkstation.text = _user.workstation.name
        viewBinding.txtPhoneNumber.text = _user.phone_number
    }
}