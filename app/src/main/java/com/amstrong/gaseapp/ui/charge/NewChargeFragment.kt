package com.amstrong.gaseapp.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.recycler_rows.ReceiptRow
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.amstrong.gaseapp.data.db.entities.Payment
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.databinding.FragmentNewChargeBinding
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.*


class NewChargeFragment : BaseFragment<MainViewModel, FragmentNewChargeBinding, MainRepository>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentReceipt: Receipt? = null

        binding.progressBar.visible(false)

        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    currentReceipt = it.value
                    updateUI(it.value)
                }
                is Resource.Failure -> {
                }
            }
        })

        viewModel.paidReceipt.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    toast(resources.getString(R.string.payment_done))
                    viewModel.declareReceipt(it.value.id)
                }
                is Resource.Failure -> {
                    toast(resources.getString(R.string.error_during_payment))
                }
            }
        })

        viewModel.normalizedReceipt.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    toast(resources.getString(R.string.ticket_declared))
                    //viewModel.printReceipt(it.value.id)
                }
                is Resource.Failure -> {
                    toast(resources.getString(R.string.error_during_normalization))
                }
            }
        })


        /*
        viewModel.receiptPrintMessage.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    toast(resources.getString(R.string.ticket_successfully_printed))
                }
                is Resource.Failure -> {
                    toast(resources.getString(R.string.error_during_printing))
                }
            }
        })
        * */



        binding.btnCharge.setOnClickListener {
            currentReceipt?.let { receipt ->
                if (binding.txtCashRecieved.text.toString().isNullOrEmpty()) {
                    binding.txtCashRecieved.setError(resources.getString(R.string.donnee_requise))
                    return@setOnClickListener
                }

                if (binding.txtCashRecieved.text.toString().reverseDecimalFormat().toFloat() >= receipt.line_items.sumByFloat { it.price }) {
                    val payementTypeId = resources.getString(R.string.key_payement_type)

                    receipt.payments.add(
                            Payment(null, payementTypeId, currentReceipt?.id, binding.txtCashRecieved.text.toString().reverseDecimalFormat().toFloat(), false)
                    )

                    viewModel.payReceipt(receipt.id, receipt.payments)
//                    viewModel.declareReceipt(receipt.id)
                    val action = NewChargeFragmentDirections.navigateToPayReceiptFragment(binding.txtCashRecieved.text.toString().reverseDecimalFormat())
                    it.findNavController().navigate(action)
                } else {
                    binding.txtCashRecieved.setError(resources.getString(R.string.montant_insuffisant))
                    receipt.payments.clear()
                }
            }
        }

    }


    private fun updateUI(receipt: Receipt?) {
        with(binding) {
            if (receipt !=null){
                txtAmountDue.text = receipt.line_items.sumByFloat { it.price }.toDecimalFormat()
                txtCashRecieved.setText(receipt.line_items.sumByFloat { it.price }.toDecimalFormat())
            }else{
                txtAmountDue.text = "0"
                txtCashRecieved.setText("0")
            }

        }
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentNewChargeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private val onItemClickListener = OnItemClickListener { item, view ->
        item as ReceiptRow


    }

    private val onItemLongClickListener = OnItemLongClickListener { item, _ ->
//        if (item is CardItem && !item.text.isNullOrBlank()) {
//            Toast.makeText(this@MainActivity, "Long clicked: " + item.text, Toast.LENGTH_SHORT).show()
//            return@OnItemLongClickListener true
//        }
        false
    }

}