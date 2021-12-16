package com.amstrong.gaseapp.ui.waiter

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentWaiterBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogMyReceipts
import com.amstrong.gaseapp.ui.dialogs.DiablogRecordReceipt
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.toLineItem
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class WaiterFragment : BaseFragment<MainViewModel, FragmentWaiterBinding, MainRepository>(), DiablogRecordReceipt.FireReceipt {

    private var receipt: Receipt? = null
    private lateinit var diningOption: String
    private var receiptWasOnceRegistered = false
    private lateinit var myline_items : ArrayList<LineItem>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLineItemWithTaxesFromRoom()

        viewModel.lineItemsWithTaxes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    updateUI(it.value.toLineItem())
                }
                is Resource.Loading -> {

                }
            }
        })


        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }
                is Resource.Loading -> {

                }

            }
        })

        with(binding){
            btnSave.setOnClickListener (onClickListener)
            btnAddition.setOnClickListener (onClickListener)
        }

        //Dialog RecordReceipy
        viewModel.diningOption.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    this.diningOption = it.value
                }

                is Resource.Failure ->{

                }
            }
        })

        viewModel.receiptSaved.observe(viewLifecycleOwner, Observer {

            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    toast("Ticket enregistré")

                }

                is Resource.Failure -> handleApiError(it) {
                    toast(resources.getString(R.string.error_check_your_network)+"\n ${it.errorBody}")
                    //saveReciept()
                }
            }
        })
    }

    private fun updateUI(lineItems: List<LineItem>) {
        myline_items =  arrayListOf()
        with(binding) {
            if (lineItems.size>0) btnSave.setText(resources.getString(R.string.enregistrer)) else btnSave.setText(resources.getString(R.string.ticket_ouvert))

            if (lineItems.size<=0) btnAddition.visible(false) else btnAddition.visible(true)
            //

            receiptWasOnceRegistered = lineItems.any { it.receipt_id !=null }
            myline_items.addAll(lineItems)
        }
    }

    private fun updateUI(receipt: Receipt?) {
        this.receipt = receipt
        if(receipt!=null){
            updateUI(receipt.line_items)
        }else{

        }
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentWaiterBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api,db)
    }

    private val onClickListener = View.OnClickListener {
        val mainActivity = requireActivity()
        mainActivity as MainActivity

        when(it.id){
            R.id.btn_save -> {
                if (binding.btnSave.text == (resources.getString(R.string.ticket_ouvert))) {

                    viewModel.getReceipts()
                    parentFragment?.let { parent -> DiablogMyReceipts().show(parent.childFragmentManager, tag) };

                }else if(binding.btnSave.text == (resources.getString(R.string.enregistrer))){
                    viewModel.getLineItemWithTaxesFromRoom()

                    if (receiptWasOnceRegistered){
                        saveReciept()
                    }else{
                        parentFragment?.let { parent -> DiablogRecordReceipt(this).show(parent.childFragmentManager, tag) };
                    }
                }

                true
            }

            R.id.btn_addition -> {
                val action = WaiterFragmentDirections.navigateToTheCheckFragment()
                it.findNavController().navigate(action)

                true
            }
            else -> false
        }
    }


    private fun saveReciept() {
        val storeId = resources.getString(R.string.key_store)

        myline_items.filter { it.receipt_id==null }.map { it.asked_at = Date() }

        if (receipt != null){
            receipt!!.apply {
                store_id =storeId
                dining_option = diningOption
                line_items = myline_items
            }
            //receipt!!.line_items = myline_items
            viewModel.saveReceipt(receipt!!)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.receipt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clean_ticket -> {
                viewModel.truncateLineItemsFromRoom()
                true
            }

            R.id.action_divide_ticket -> {
                val action = WaiterFragmentDirections.navigateToReceiptToDivideFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun fire(receipt: Receipt) {
        this.receipt = receipt
        saveReciept()
    }


}