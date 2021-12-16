package com.amstrong.gaseapp.ui.waiter

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.db.entities.ReceiptsSourceDestination
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentReceiptToDivideBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.recycler_rows.LineItemWithTaxesRow
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.amstrong.gaseapp.utils.sumByFloat
import com.amstrong.gaseapp.utils.toLineItemWithTaxes
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class ReceiptToDivideFragment : BaseFragment<MainViewModel, FragmentReceiptToDivideBinding, MainRepository>() {
    private var receiptSource: Receipt? = null
    private var receiptDestination: Receipt? = null
    private var receiptsSourceDestination: ReceiptsSourceDestination? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rowTotal1?.getRoot()?.visible(false)
        viewModel.receiptSource.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {

                    receiptSource = it.value
                    updateUISource(receiptSource)

                    if (receiptDestination == null) {
                        val receiptDest = Receipt(
                                it.value.note,
                                it.value.order,
                                it.value.dining_option,
                                arrayListOf(),
                                it.value.store_id,
                                Date(),
                                Date(),
                        )
                        receiptDest.waiter_id = it.value.waiter_id
                        receiptDest.waiter_name = it.value.waiter_name

                        viewModel.selectReceiptDestination(receiptDest)
                    }
                }
            }
        })

        viewModel.receiptDestination.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    receiptDestination = it.value
                    updateUIDestination(it.value)
                }
            }
        })

        viewModel.receiptSourceDestination.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success ->{
                    toast(resources.getString(R.string.ticket_successfully_split))
                }
            }
        })
    }

    private fun updateUISource(receipt: Receipt?) {
        val groupedLines = arrayListOf<LineItem>()

        if (receipt!=null) {
            val distinctLine = receipt.line_items.distinctBy { it.variant_id }

            for (line: LineItem in distinctLine) {
                groupedLines.add(line.copy(quantity = (receipt.line_items.count { it.variant_id == line.variant_id }).toFloat()))
            }

            with(binding) {
                txtNameReceipt1.setText(receipt.order)

                if (receipt.line_items.any()) rowTotal1?.getRoot()?.visible(true) else rowTotal1?.getRoot()?.visible(false)
                rowTotal1.txtName.text = "Total"
                rowTotal1.txtQte.visible(false)
                rowTotal1.txtAmount.text = receipt.line_items.sumByFloat { it.price }.toDecimalFormat()

                val mAdapter = GroupAdapter<ViewHolder>().apply {
                    addAll(groupedLines.toLineItemWithTaxes().toLineItemWithTaxesRow())
                    setOnItemClickListener(onItemClickListener1)
                }

                recyclerView1.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = mAdapter
                }
            }
        }


    }

    private fun updateUIDestination(receipt: Receipt?) {
        val groupedLines = arrayListOf<LineItem>()

        if (receipt!=null){
            val distinctLine = receipt.line_items.distinctBy { it.variant_id }

            for (line: LineItem in distinctLine) {
                groupedLines.add(line.copy(quantity = (receipt.line_items.count { it.variant_id == line.variant_id }).toFloat()))
            }

            with(binding) {
                txtNameReceipt2.setText(receipt.order+" - 1")

                if (receipt.line_items.any()) rowTotal2.getRoot().visible(true) else rowTotal2.getRoot().visible(false)
                rowTotal2.txtName.text = "Total"
                rowTotal2.txtQte.visible(false)
                rowTotal2.txtAmount.text = receipt.line_items.sumByFloat { it.price }.toDecimalFormat()

                val mAdapter = GroupAdapter<ViewHolder>().apply {
                    addAll(groupedLines.toLineItemWithTaxes().toLineItemWithTaxesRow())
                    setOnItemClickListener(onItemClickListener2)
                }

                recyclerView2.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = mAdapter
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.receipt_to_divide, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (receiptSource != null && receiptDestination != null) {
                    receiptSource?.order = binding.txtNameReceipt1.text.toString()
                    receiptDestination?.order = binding.txtNameReceipt2.text.toString()

                    receiptDestination?.line_items?.map { it.receipt_id = null }
                    receiptDestination?.line_items?.map { it.id = null }

                    viewModel.divideReceipt(receiptSource!!.id, ReceiptsSourceDestination(receiptSource!!, receiptDestination!!))

//                    for (line: LineItem in receiptDestination!!.line_items) {
//                        Log.d(TAG, "onOptionsItemSelected: line_items receipt_id = ${line.receipt_id}")
//                    }
                    return true
                }
                return false
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReceiptToDivideBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private val onItemClickListener1 = OnItemClickListener { row, view ->
        transfertGroupedLines((row as LineItemWithTaxesRow).lineItemWithTaxes.lineItem.variant_id,true)
    }

    private val onItemClickListener2 = OnItemClickListener { row, view ->
        transfertGroupedLines((row as LineItemWithTaxesRow).lineItemWithTaxes.lineItem.variant_id,false)
    }

//    private fun List<LineItem>.toOrderLineRow(): List<LineItemWithTaxesRow> {
//        return this.map {
//            LineItemWithTaxesRow(it)
//        }
//    }

    private fun List<LineItemWithTaxes>.toLineItemWithTaxesRow(): List<LineItemWithTaxesRow> {
        return this.map {
            LineItemWithTaxesRow(it)
        }
    }



    private fun transfertGroupedLines(variantId: String, forward: Boolean) {
        if (receiptDestination!=null && forward){
            val linesTobeDeleted = receiptSource!!.line_items.filter { it.variant_id == variantId }
            viewModel.transfertLineItem2Destination(linesTobeDeleted)
        }

        if (receiptSource!=null && !forward){
            val linesTobeDeleted = receiptDestination!!.line_items.filter { it.variant_id == variantId }
            viewModel.transfertLineItem2Source(linesTobeDeleted)
        }

        receiptsSourceDestination = ReceiptsSourceDestination(receiptSource!!, receiptDestination!!)
    }


}