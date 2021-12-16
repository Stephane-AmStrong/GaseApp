package com.amstrong.gaseapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Item
import com.amstrong.gaseapp.data.db.entities.Order
import com.amstrong.gaseapp.data.db.entities.OrderLine
import com.amstrong.gaseapp.data.db.entities.Variant
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogAddOrderLineBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.adapters.VariantAdapter
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*


class DialogAddOrderLine : BaseBottomSheet<MainViewModel, DiablogAddOrderLineBinding, MainRepository>() {

    companion object {
        fun newInstance() = DialogAddOrderLine()
    }

    private var _orderLine: OrderLine? = null
    private var _variant: Variant? = null
    private var employeeId: String? = null
    private var _order: Order? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.setTitle(R.string.menu_add_order_line)

        binding.btnClose.setOnClickListener {
            dismiss()
        }


        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_add_order_line -> {
                    addLine()
                    true
                }

                R.id.action_save_as_draft -> {

                    true
                }

                R.id.action_cancel -> {

                    true
                }

                else -> false
            }
        }


        binding.progressBar.visible(false)
        clearToolbarMenu()


        viewModel.selectedOrder.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    updateUIOrder(it.value)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.selectedOrderLine.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUIOrderLine(it.value)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.allItems.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUI(it.value)
                }

                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }

                is Resource.Failure -> handleApiError(it) {
//                    viewModel.getVariants()
                }
            }
        })

        binding.txtItem.setOnItemClickListener() { adapterView, view, position, id ->
            _variant = adapterView.adapter.getItem(position) as Variant?

            with(binding) {
                txtItem.setText(_variant?.item_name)
                txtInStock.setText(_variant?.in_stock?.toDecimalFormat())
                txtAverageCost.setText(_variant?.cost?.toDecimalFormat())
                txtCost.setText("")
                txtQuantityOrdered.setText("1")
                txtQuantityReveived.setText("0")
                calculateAmount()
                //txtAmount.setText((txtQuantity.text.toString().toInt()*variant?.cost!!).toDecimalFormat())
            }

        }


        binding.txtItem.addTextChangedListener {
            try {
                if (ready()) showToolbarMenu() else clearToolbarMenu()
            } catch (ex: Exception) {
                binding.txtQuantityOrdered.setText("0")
            }
            calculateAmount()
        }

        binding.txtCost.addTextChangedListener {
            try {
                if (ready()) showToolbarMenu() else clearToolbarMenu()
            } catch (ex: Exception) {
                binding.txtCost.setText("0")
            }
            calculateAmount()
        }

        binding.txtQuantityOrdered.addTextChangedListener {
            try {
                if (ready()) showToolbarMenu() else clearToolbarMenu()
            } catch (ex: Exception) {
                binding.txtQuantityOrdered.setText("0")
            }
            calculateAmount()
        }

        binding.txtQuantityReveived.addTextChangedListener {
            try {
                if (ready()) showToolbarMenu() else clearToolbarMenu()
            } catch (ex: Exception) {
                binding.txtQuantityReveived.setText("0")
            }
        }


    }

    private fun calculateAmount() {
        val qte = if (binding.txtQuantityOrdered.text.toString().isBlank()) 0f else binding.txtQuantityOrdered.text.toString().replace(",", ".").toFloat()
        //val cost = if (binding.txtCost.text.toString().isBlank()) 0f else _variant?.cost!!.toFloat()
        //val cost = if (binding.txtCost.text.toString().isBlank()) 0f else binding.txtCost.text.toString().toDigitFromContext(requireContext()).replace(",",".").toFloat()
        val cost = if (binding.txtCost.text.toString().isBlank()) 0f else binding.txtCost.text.toString().reverseDecimalFormat().toFloat()

        if (_variant != null) {
            binding.txtAmount.setText((qte * cost).toDecimalFormat())
        }
    }

    private fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    private fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.add_order_line
        )
    }


    private fun updateUIOrder(order: Order?) {
        this._order = order
    }


    private fun updateUIOrderLine(orderLine: OrderLine?) {
        _orderLine = orderLine
        _variant = orderLine?.variant

        with(binding) {
            txtItem.setText(orderLine?.variant?.item_name)
            txtInStock.setText(orderLine?.variant?.in_stock?.toDecimalFormat())
            txtAmount.setText(orderLine?.amount?.toDecimalFormat())
            txtQuantityOrdered.setText(orderLine?.quantity_order?.toDecimalFormat())
            txtQuantityReveived.setText(orderLine?.quantity_received?.toDecimalFormat())
            txtCost.setText(orderLine?.purchase_cost?.toDecimalFormat())
        }
    }


    private fun updateUI(items: List<Item>) {
        val variants = arrayListOf<Variant>()

        for (item: Item in items) {
            variants.addAll(item.variants)
        }

        val adapter = VariantAdapter(requireContext(), R.layout.item_auto_complete_text_view, variants)
        binding.txtItem.setAdapter(adapter)
        binding.txtItem.threshold = 2
    }

    private fun ready(): Boolean {
        with(binding) {
            return !(txtItem.text.toString().isBlank() && txtCost.text.toString().isBlank() && txtQuantityOrdered.text.toString().isBlank() && txtQuantityReveived.text.toString().isBlank())
        }
    }


    private fun isOk(): Boolean {
        with(binding) {
            if (txtItem.text.toString().isEmpty()) {
                txtItem.setError(resources.getString(R.string.error_field_required))
            }

            if (txtCost.text.toString().isEmpty()) {
                txtCost.setError(resources.getString(R.string.error_field_required))
            }

            if (txtQuantityOrdered.text.toString().isEmpty()) {
                txtQuantityOrdered.setError(resources.getString(R.string.error_field_required))
            }

            if (txtQuantityReveived.text.toString().isEmpty()) {
                txtQuantityReveived.setError(resources.getString(R.string.error_field_required))
            }

            if (!txtQuantityOrdered.text.toString().isEmpty() && !!txtQuantityReveived.text.toString().isEmpty() && (txtQuantityOrdered.text.toString().toFloat() != txtQuantityReveived.text.toString().toFloat())) {
                txtAverageCost.setError(resources.getString(R.string.error_qtes_dont_match))
            }

            return !(txtItem.text.toString().isEmpty() || txtAverageCost.text.toString().isEmpty() || txtCost.text.toString().isEmpty() || txtQuantityOrdered.text.toString().isEmpty() || txtQuantityReveived.text.toString().isEmpty() || (txtQuantityOrdered.text.toString().toFloat() != txtQuantityReveived.text.toString().toFloat()))
        }

    }


    private fun addLine() {
        if (isOk() && _variant != null) {

            with(binding) {
                if (_orderLine?.id == null) {
                    val orderLine = OrderLine(null, "", _variant!!, txtQuantityOrdered.text.toString().toFloat(), txtQuantityReveived.text.toString().toFloat(), txtCost.text.toString().toFloat(), txtCost.text.toString().toFloat() * txtQuantityOrdered.text.toString().toFloat(), Date())

                    _order?.order_lines?.add(orderLine)
                } else {
                    _orderLine?.apply {
                        variant = _variant!!
                        quantity_order = txtQuantityOrdered.text.toString().toFloat()
                        quantity_received = txtQuantityReveived.text.toString().toFloat()
                        //purchase_cost = _variant?.cost!!
                        //amount = _variant?.cost!! * txtQuantityOrdered.text.toString().toFloat()
                        purchase_cost = txtCost.text.toString().reverseDecimalFormat().toFloat()
                        amount = txtCost.text.toString().toFloat() * txtQuantityOrdered.text.toString().toFloat()
                        update_at = Date()
                    }
                    viewModel.updateOrderLine(_orderLine!!)
                }

                viewModel.setOrder(_order)

                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.setOrderLine(null)
        _orderLine = null
        _variant = null
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogAddOrderLineBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

}