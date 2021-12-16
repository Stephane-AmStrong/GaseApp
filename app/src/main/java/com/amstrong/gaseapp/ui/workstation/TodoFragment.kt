package com.amstrong.gaseapp.ui.workstation

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentTodoBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogEmployees
import com.amstrong.gaseapp.ui.recycler_rows.ItemOrderedRow
import com.amstrong.gaseapp.utils.toLineItemWithTaxes
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class TodoFragment : BaseFragment<MainViewModel, FragmentTodoBinding, MainRepository>() {

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    protected lateinit var workstationId: String
    protected lateinit var workstationName: String
    protected lateinit var myId: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include.progressBar.visible(false)
        (activity as AppCompatActivity?)?.supportActionBar?.title = workstationName

        viewModel.getReceipts()

        viewModel.receipts.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)
                    updateUI(it.value)

//                    mainActivity.notify("commandes", "commandes en attentes", R.drawable.ic_baseline_outdoor_grill_24)

                }
                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }
            }
        })


        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                mainHandler.postDelayed(this, 900)
                try {
                    mAdapter.notifyDataSetChanged()
                } catch (ex: Exception) {

                }
            }
        })

    }

    private fun updateUI(receipts: List<Receipt>) {
        val line_items = arrayListOf<LineItem>()
        for (receipt: Receipt in receipts.filter { it.cancelled_at == null }) {
            receipt.line_items.map { it.Receipt = receipt }
            line_items.addAll(receipt.line_items.filter { it.caterer_id == null })
        }

        initRecyclerView(line_items.filter { it.workstation_id == workstationId }.sortedBy { it.asked_at })
    }


//    private fun updateUI(receipt: Receipt?) {
//        if (receipt!=null){
//            val line_items = arrayListOf<LineItem>()
//
//            line_items.addAll(receipt.line_items.filter { it.caterer_id == null })
//
//            val groupLines = arrayListOf<LineItem>()
//            val distinctLines= line_items.distinctBy { it.variant_id }.filter { it.workstation_id == workstationId }
//
//            for(line: LineItem in distinctLines) groupLines.add(line.copy(quantity = line_items.count { it.variant_id == line.variant_id }.toFloat()))
//
//            initRecyclerView(groupLines)
//        }else{
//            //binding.include.recyclerView.clear
//        }
//    }


    private fun initRecyclerView(lineItems: List<LineItem>) {

        with(binding){
            mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(lineItems.toCookOrderLineRow())
                setOnItemClickListener(onItemClickListener)
            }

            include.recyclerView.apply {
                layoutManager = GridLayoutManager(context, requireContext().resources.getInteger(R.integer.number_of_grid_items_order))
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentTodoBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        workstationId = runBlocking { userPreferences.workstation_id.first()!! }
        workstationName = runBlocking { userPreferences.employeeWorkstation.first()!! }
        myId = runBlocking { userPreferences.employeeId.first()!! }

        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private val onItemClickListener = OnItemClickListener{ row, view ->
        val cookActivity = requireActivity()
        cookActivity as MainActivity
        row as ItemOrderedRow

        viewModel.selectLineItem(row.lineItem.toLineItemWithTaxes())
        viewModel.getWorkstation(workstationId)
        parentFragment?.let { parent -> DiablogEmployees.newInstance(false).show(parent.childFragmentManager, tag) }
    }

    private fun List<LineItem>.toCookOrderLineRow() : List<ItemOrderedRow>{
        return this.map {
            ItemOrderedRow(it)
        }
    }
}