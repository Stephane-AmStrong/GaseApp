package com.amstrong.gaseapp.ui.waiter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentListReceiptsBinding
import com.amstrong.gaseapp.databinding.FragmentOrdeDetailsBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogMyReceipts
import com.amstrong.gaseapp.ui.dialogs.DiablogRetrievalConfirmation
import com.amstrong.gaseapp.ui.recycler_rows.MyOrdersLineItemRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class OrdeDetailsFragment : BaseFragment<MainViewModel, FragmentOrdeDetailsBinding, MainRepository>() {

    private lateinit var myworkstation_id: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include.progressBar.visible(false)

        viewModel.receipt.observe(viewLifecycleOwner, Observer { receipt ->
            binding.include.progressBar.visible(receipt is Resource.Loading)

            when (receipt) {
                is Resource.Success -> {
                    //if (receipt.value!=null && receipt.value.line_items.any{it.ready_at!=null && it.served_at == null}) mainActivity.notify("commandes traité", "", R.drawable.ic_baseline_outdoor_grill_24)
                    binding.include.progressBar.visible(false)
                    updateUI(receipt.value)
                }
            }
        })

    }

    private fun updateUI(receipt: Receipt?) {

        with(binding){
            if (receipt!=null){
                (activity as AppCompatActivity?)?.supportActionBar?.title = receipt.order

                val mAdapter = GroupAdapter<ViewHolder>().apply {
                    addAll(receipt.line_items.toMyOrdersLineItemRow())
                    setOnItemClickListener(onItemClickListener)
                }

                include.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = mAdapter
                }
            }else{

            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentOrdeDetailsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        myworkstation_id = runBlocking { userPreferences.workstation_id.first()!! }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

    private val onItemClickListener = OnItemClickListener{lineItemRow, view ->
        lineItemRow as MyOrdersLineItemRow


        if (lineItemRow.lineItem.caterer_id== null){
            toast("'${lineItemRow.lineItem.item_name}' ${resources.getString(R.string.error_dish_not_ready_yet)}")
        }else{
            viewModel.selectLineItem(lineItemRow.lineItem)
            parentFragment?.let { parent -> DiablogRetrievalConfirmation().show(parent.childFragmentManager, tag) }
        }
    }


    private fun List<LineItem>.toMyOrdersLineItemRow(): List<MyOrdersLineItemRow> {
        return this.map {
            MyOrdersLineItemRow(it)
        }
    }

}