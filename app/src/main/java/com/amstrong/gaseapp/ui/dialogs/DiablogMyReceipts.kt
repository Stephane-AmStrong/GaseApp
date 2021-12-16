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
import com.amstrong.gaseapp.databinding.DiablogMyReceiptsBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.recycler_rows.ReceiptCheckRow
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


class DiablogMyReceipts : BaseBottomSheet<MainViewModel, DiablogMyReceiptsBinding, MainRepository>(), ReceiptCheckRow.MyOnCheckedChanged {

    private var canManageHall: Boolean = false
    private lateinit var myId: String
    private var canCashIn: Boolean = false
    private lateinit var _receipts: ArrayList<Receipt>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){

            toolbar.setTitle(R.string.selectionner_ticket)

            include.progressBar.visible(false)

            //viewModel.getReceiptsOfWaiter(null)
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

            btnClose.setOnClickListener {
                dismiss()
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_delete_ticket -> {
                        toast("action_delete_ticket")

                        true
                    }
                    R.id.action_merge_ticket -> {

                        parentFragment?.let { parent -> DiablogReceiptDestinations().show(parent.childFragmentManager, tag) }

                        dismiss()
                        true
                    }
                    R.id.action_forward_ticket -> {
                        viewModel.getUsers()
                        parentFragment?.let { parent -> DiablogEmployees.newInstance(true).show(parent.childFragmentManager, tag) }
                        dismiss()
                        true
                    }
                    else -> false
                }
            }



        }



    }

    fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                if (canManageHall) R.menu.my_tickets_full_option else R.menu.my_tickets
        )
    }


    private fun updateUI(receipts: List<Receipt>) {
        _receipts = arrayListOf<Receipt>()
        _receipts.addAll(receipts)

        with(binding){
            lateinit var mAdapter: GroupAdapter<ViewHolder>

            mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(_receipts.filter {
                    it.waiter_id == myId && it.closed_at == null && it.cancelled_at == null

                }.toWaiterReceiptRow())

                if (canCashIn){
                    addAll(receipts.filter {
                        it.closed_at != null && it.payments.isEmpty()
                    }.toWaiterReceiptRow())
                }

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
    ) = DiablogMyReceiptsBinding.inflate(inflater, container, false)

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
        row as ReceiptCheckRow

        viewModel.selectReceipt(row.receipt)
        viewModel.selectReceiptSource(row.receipt)

        viewModel.saveLineItemsToRoom(row.receipt.line_items)


        dismiss()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_tickets, menu)
    }

    private fun List<Receipt>.toWaiterReceiptRow() : List<ReceiptCheckRow>{
        return this.map {
            ReceiptCheckRow(it, this@DiablogMyReceipts)
        }
    }

    override fun selectionChanged(isChecked: Boolean, position: Int) {

        _receipts[position].isSelected = isChecked
        if (_receipts.any { it.isSelected }){
            showToolbarMenu()
        }else{
            clearToolbarMenu()
        }


        val selectedReceiptIds:List<String> = _receipts.filter{ receipt -> receipt.isSelected }.map{ receipt-> receipt.id!! }
        viewModel.selectReceiptIds(selectedReceiptIds)
        _receipts.map { receipt -> receipt.isSelected = false }
    }


}
