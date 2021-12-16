package com.amstrong.gaseapp.ui.dialogs

import android.content.Context
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
import com.amstrong.gaseapp.databinding.DiablogRecordReceiptBinding
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.toLineItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*


class DiablogRecordReceipt(private val listener: FireReceipt) :
        BaseBottomSheet<MainViewModel, DiablogRecordReceiptBinding, MainRepository>() {

    private var receipt: Receipt? = null
    private lateinit var diningOption: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setTitle(R.string.enregistrer_ticket)

        binding.btnClose.setOnClickListener {
            dismiss()
            clearFields()
        }


        binding.toolbar.inflateMenu(R.menu.dialog_register)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_register -> {
                    saveReciept()
                    true
                }

                else -> false
            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogRecordReceiptBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private fun saveReciept() {
        val storeId = resources.getString(R.string.key_store)

        receipt = Receipt(
                binding.txtReceiptComment.text.toString(),
                binding.txtReceiptName.text.toString(),
                arrayListOf(),
                storeId,
                Date(),
                Date(),
        )

        if(receipt!=null) {
            listener.fire(receipt!!)
            dismiss()
        }
    }


    private fun clearFields() {
        with(binding) {
            txtReceiptName.setText("")
            txtReceiptComment.setText("")
        }
    }

    interface FireReceipt {
        fun fire(receipt: Receipt)
    }
}