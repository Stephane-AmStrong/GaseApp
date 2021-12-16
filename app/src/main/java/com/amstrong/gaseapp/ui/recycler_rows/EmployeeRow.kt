package com.amstrong.gaseapp.ui.recycler_rows

import android.widget.CheckBox
import android.widget.RadioButton
import androidx.cardview.widget.CardView
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.databinding.RowEmployeeBinding
import com.xwray.groupie.databinding.BindableItem


class EmployeeRow (
        private val _employee: Employee,
) : BindableItem<RowEmployeeBinding>(){

    private lateinit var _cardView : CardView
    override fun getLayout() = R.layout.row_employee

    val employee: Employee
        get() = _employee

    val cardView: CardView
        get() = _cardView

    override fun bind(viewBinding: RowEmployeeBinding, position: Int) {
        viewBinding.checkBoxEmployeeName.text = _employee.name
        viewBinding.checkBoxEmployeeName.isChecked = _employee.isSelected
        _cardView = viewBinding.rowEmployee
    }


}