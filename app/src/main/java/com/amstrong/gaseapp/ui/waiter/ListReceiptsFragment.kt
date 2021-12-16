package com.amstrong.gaseapp.ui.waiter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentListReceiptsBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogCancelReceipt
import com.amstrong.gaseapp.ui.recycler_rows.ReceiptRow
import com.amstrong.gaseapp.utils.formatToPattern
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.google.android.material.datepicker.MaterialDatePicker
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class ListReceiptsFragment : BaseFragment<MainViewModel, FragmentListReceiptsBinding, MainRepository>(){

    private lateinit var myworkstation_id: String
    private var canTakeOrders: Boolean = false
    private var canManageHall : Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.menu_my_orders)

        if (canManageHall) viewModel.getReceipts() else viewModel.getReceiptsOfWaiter(null)
        binding.txtSelectedDate.setText(Date().formatToPattern())



        // now create instance of the material date picker
        // builder make sure to add the "datePicker" which
        // is normal material date picker which is the first
        // type of the date picker in material design date
        // picker
        val materialDateBuilder: MaterialDatePicker.Builder<*> =
                MaterialDatePicker.Builder.datePicker()

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText(resources.getString(R.string.selectionner_date))

        // now create the instance of the material date
        // picker
        val materialDatePicker = materialDateBuilder.build()

        // handle select date button which opens the
        // material design date picker
        binding.btnPickDate?.setOnClickListener( View.OnClickListener {
            // getSupportFragmentManager() to
            // interact with the fragments
            // associated with the material design
            // date picker tag is to get any error
            // in logcat
            materialDatePicker.show(childFragmentManager, "MATERIAL_DATE_PICKER")
        })

        // now handle the positive button click from the
        // material design date picker
        materialDatePicker.addOnPositiveButtonClickListener {
            // if the user clicks on the positive
            // button that is ok button update the
            // selected date
            try {
                binding.txtSelectedDate?.setText(materialDatePicker.headerText)
                viewModel.getReceiptsOfWaiter(materialDatePicker.headerText)
            }catch (ex : Exception){

            }
            // in the above statement, getHeaderText
            // is the selected date preview from the
            // dialog
        }

        //









        binding.include.progressBar.visible(false)

//        if (!canTakeOrders){
//            viewModel.receipts.observe(viewLifecycleOwner, Observer {
//                when (it) {
//                    is Resource.Success -> {
//                        binding.include.progressBar.visible(false)
//                        updateUI4AllReceipt(myworkstation_id, it.value)
//                    }
//                    is Resource.Loading -> {
//                        binding.include.progressBar.visible(true)
//                    }
//                }
//            })
//        }


        if (canManageHall){
            viewModel.receipts.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resource.Success -> {
                        binding.include.progressBar.visible(false)
                        updateUI4AllReceipt(it.value)
                    }

                    is Resource.Loading -> {
                        binding.include.progressBar.visible(true)
                    }
                }
            })
        }



        viewModel.myReceipts.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)
                    updateUIWithMyReceipts(it.value)
                }
                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }
            }
        })

    }

//    private fun updateUI4AllReceipt(workstation_id: String, receipts: List<Receipt>) {
//        val accessReceipts = arrayListOf<Receipt>()
//        for (receipt: Receipt in receipts) {
//            if(receipt.line_items.any {it.workstation_id == workstation_id && it.caterer_id == null}) accessReceipts.add(receipt)
//        }
//
//        with(binding){
//            val mAdapter = GroupAdapter<ViewHolder>().apply {
//                addAll(accessReceipts.toReceiptRow())
//                setOnItemClickListener(onItemClickListener)
//            }
//
//            include.recyclerView.apply {
//                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
//                adapter = mAdapter
//            }
//        }
//    }


    private fun updateUI4AllReceipt(receipts: List<Receipt>) {

        with(binding){
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(receipts.filter { it.cancelled_at == null && it.closed_at == null }.toReceiptRow())
                setOnItemClickListener(onItemClickListener)
            }

            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }


    private fun updateUIWithMyReceipts(receipts: List<Receipt>) {
        with(binding){
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(receipts.toReceiptRow())
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
    ) = FragmentListReceiptsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        myworkstation_id = runBlocking { userPreferences.workstation_id.first()!! }
        canTakeOrders = runBlocking { userPreferences.canTakeOrders.first()!! }
        canManageHall = runBlocking { userPreferences.canManageHall.first()!! }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

    private val onItemClickListener = OnItemClickListener{row, view ->
        row as ReceiptRow

        viewModel.getReceipt(row.receipt.id)
        val action = ListReceiptsFragmentDirections.navigateToOrderDetailsFragment()
        view.findNavController().navigate(action)

        /*
        val action = TheCheckFragmentDirections.navigateToWaiterFragment()
                    it.findNavController().navigate(action)
        */

        if(canManageHall){
            viewModel.setReceipt(row.receipt)
            parentFragment?.let { parent -> DiablogCancelReceipt().show(parent.childFragmentManager, tag) }
        }
    }

    private fun List<Receipt>.toReceiptRow() : List<ReceiptRow>{
        return this.map {
            ReceiptRow(it)
        }
    }

}