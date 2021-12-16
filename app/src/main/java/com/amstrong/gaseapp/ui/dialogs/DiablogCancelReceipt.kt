package com.amstrong.gaseapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogCancelReceiptBinding
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*


class DiablogCancelReceipt :
        BaseBottomSheet<MainViewModel, DiablogCancelReceiptBinding, MainRepository>(){

    protected var _receipt: Receipt? = null
    protected lateinit var diningOption: String
    protected var myline_items = arrayListOf<LineItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
            clearFields()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_cancel_ticket -> {
                    cancelReciept()
                    true
                }

                else -> false
            }
        }

        binding.progressBar.visible(false)
        viewModel.selectedReceipt.observe(viewLifecycleOwner, Observer { receipt ->
            binding.progressBar.visible(receipt is Resource.Loading)
            when (receipt) {
                is Resource.Success -> {
                    updateUI(receipt.value)
                }
            }
        })
    }

    private fun updateUI(receipt: Receipt?) {
        _receipt = receipt

        with(binding){
            txtReceiptName.setText(receipt?.order)
            txtReceiptComment.setText(receipt?.note)
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogCancelReceiptBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private fun cancelReciept() {
        val storeId = resources.getString(R.string.key_store)

        if (_receipt!= null){
            _receipt!!.apply {
                cancelled_at = Date()
            }
        }

        viewModel.saveReceipt(_receipt!!)
        dismiss()
    }


    private fun clearFields() {
        with(binding){
            txtReceiptName.setText("")
            txtReceiptComment.setText("")
        }
    }
}