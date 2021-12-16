package com.amstrong.gaseapp.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogLineItemBinding
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.waiter.AuMenuFragment
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.lang.Math.abs


class DiablogLineItem :
        BaseBottomSheet<MainViewModel, DiablogLineItemBinding, MainRepository>() {

    protected var diningOption: String? = null
    lateinit var selectedLineItem: LineItem
    private var linesCount: Int = 1
    private val lineItems = arrayListOf<LineItemWithTaxes>()
    private var canManageHall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        diningOption = arguments?.getString(ARG_DINING_OPTION);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.dialog_register)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_register -> {
                    saveLine()
                    true
                }

                else -> false
            }
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.progressBar.visible(false)
        viewModel.lineItemWithTaxes.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }
                is Resource.Loading -> {
                }
            }
        })

        viewModel.lineItemsWithTaxes.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    linesCount = getMatchingLinesCount(selectedLineItem.variant_id, it.value)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)

                }

            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.releaseLineItem()
    }

    private fun getMatchingLinesCount(variantId: String?, lineItemsWithTaxes: List<LineItemWithTaxes>): Int {
        if (variantId == null) return 0
        this.lineItems.clear()
        this.lineItems.addAll(lineItemsWithTaxes)

        return lineItems.count { it.lineItem.variant_id == variantId }
    }

    private fun updateUI(lineItemWithTaxes: LineItemWithTaxes?) {
        if (lineItemWithTaxes != null) {
            selectedLineItem = lineItemWithTaxes.lineItem.copy()

            with(binding) {
                toolbar.setTitle(lineItemWithTaxes.lineItem.item_name)
                txtPrix.text = lineItemWithTaxes.lineItem.price.toDecimalFormat()
                txtQte.setText(lineItemWithTaxes.lineItem.quantity.toDecimalFormat())
            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogLineItemBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        canManageHall = runBlocking { userPreferences.canManageHall.first()!! }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private fun saveLine() {
        var updatedQte = 0f
        var iteration = 0

        with(binding) {
            updatedQte = txtQte.text.toString().toFloat()
            if (linesCount.toFloat() == updatedQte) {
                clearFields()
                dismiss()
                return
            }

            iteration = abs(linesCount - updatedQte.toInt())

            if (updatedQte > linesCount) {
                val copyOfLineItem = selectedLineItem.copy(id = null)
                for (i in 1..iteration) {
                    viewModel.saveLineItemsToRoom(copyOfLineItem)
                }
            } else {

                for (i in iteration downTo 1) {
                    if (canManageHall) {
                        viewModel.removeLineItemsFromRoom(lineItems[i].lineItem)
                    } else {
                        if (lineItems[i].lineItem.receipt_id == null) viewModel.removeLineItemsFromRoom(lineItems[i].lineItem)
                    }
                }

            }

        }
        clearFields()
        dismiss()
    }


    private fun clearFields() {
        with(binding) {
            toolbar.setTitle("")
            txtPrix.setText("")
            txtQte.setText("")
            viewModel.releaseLineItem()
        }
    }

    companion object {
        private const val ARG_DINING_OPTION = "arg_dining_option"

        @JvmStatic
        fun newInstance(diningOption: String): AuMenuFragment {
            return AuMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DINING_OPTION, diningOption)
                }
            }
        }
    }
}