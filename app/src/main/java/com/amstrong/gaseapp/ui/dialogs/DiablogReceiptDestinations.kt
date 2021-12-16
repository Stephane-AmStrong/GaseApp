package com.amstrong.gaseapp.ui.dialogs

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogReceiptDestinationsBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.recycler_rows.ReceiptDestinationRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.android.synthetic.main.row_receipt.*
import kotlinx.android.synthetic.main.row_receipt.view.*


class DiablogReceiptDestinations : BaseBottomSheet<MainViewModel, DiablogReceiptDestinationsBinding, MainRepository>(), ReceiptDestinationRow.MyOnCheckedChanged {

    private var canManageHall: Boolean = false
    private lateinit var myId: String
    private var canCashIn: Boolean = false
    private var _receipts = listOf<Receipt>()
    private var selectedReceiptIds = listOf<String>()
    private lateinit var mAdapter: GroupAdapter<ViewHolder>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){

            toolbar.setTitle(R.string.selectionner_ticket)

            include.progressBar.visible(false)

            viewModel.getReceiptsOfWaiter(null)
            viewModel.receipts.observe(viewLifecycleOwner, Observer {
                include.progressBar.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                        binding.include.progressBar.visible(false)

                        updateUI(it.value)
                    }

                    is Resource.Failure -> {
                        toast(resources.getString(R.string.error_loading_tickets))
                    }
                }
            })

            viewModel.selectedReceiptIds.observe(viewLifecycleOwner, Observer {
                include.progressBar.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                        binding.include.progressBar.visible(false)

                        selectedReceiptIds = (it.value)
                    }

                    is Resource.Failure -> {
                        toast(resources.getString(R.string.error_loading_tickets)+" sélectionnés")
                    }
                }
            })


            viewModel.mergedReceipt.observe(viewLifecycleOwner, Observer {
                include.progressBar.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                        binding.include.progressBar.visible(false)

                        toast(resources.getString(R.string.ticket_successfully_merged))
                        dismiss()
                    }

                    is Resource.Failure -> {
                        toast(resources.getString(R.string.error_merging_tickets))
                    }
                }
            })


            btnClose.setOnClickListener {
                dismiss()
            }




            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_proceed -> {
                        viewModel.mergeReceipts(_receipts.first { it.isSelected }.id, selectedReceiptIds)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.clearMergeReceipts()
    }

    fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.proceed
        )
    }


    private fun updateUI(receipts: List<Receipt>) {
        this._receipts = receipts

        with(binding){

            mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(receipts.filter {
                    it.waiter_id == myId && it.closed_at == null

                }.toReceiptDestinationRow())

//                if (canCashIn){
//                    addAll(receipts.filter {
//                        it.closed_at != null && it.payments.isEmpty()
//                    }.toReceiptDestinationRow())
//                }

                setOnItemClickListener(onItemClickListener)
            }



            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogReceiptDestinationsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        myId = runBlocking { userPreferences.employeeId.first()!! }
        canManageHall = runBlocking { userPreferences.canManageHall.first()!! }
        canCashIn = runBlocking { userPreferences.canCashIn.first()!! }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private val onItemClickListener = OnItemClickListener{ row, view ->
        row as ReceiptDestinationRow

//        viewModel.selectReceipt(row.receipt)
//        viewModel.selectReceiptSource(row.receipt)
//
//        dismiss()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_tickets, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_ticket -> {
                toast("action_delete_ticket")
                true
            }
            R.id.action_merge_ticket -> {
                toast("action_merge_ticket")
                true
            }
            R.id.action_forward_ticket -> {
                toast("action_forward_ticket")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    private fun List<Receipt>.toReceiptDestinationRow() : List<ReceiptDestinationRow>{
        return this.map {
            ReceiptDestinationRow(it, this@DiablogReceiptDestinations)
        }
    }

    override fun selectionChanged(isChecked: Boolean, position: Int) {
        _receipts.map { it.isSelected = false }

        _receipts[position].isSelected = isChecked

        if (_receipts.any { it.isSelected }){
            showToolbarMenu()
        }else{
            clearToolbarMenu()
        }

        updateUI(_receipts)
    }


}
