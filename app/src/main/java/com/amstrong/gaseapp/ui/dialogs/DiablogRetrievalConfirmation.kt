package com.amstrong.gaseapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogRetrievalConfirmationBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.utils.toLineItem
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class DiablogRetrievalConfirmation : BaseBottomSheet<MainViewModel, DiablogRetrievalConfirmationBinding, MainRepository>(){

    private lateinit var _lineItem: LineItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lineItemWithTaxes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value!=null){
                        binding.progressBar.visible(false)
                        updateUI(it.value.toLineItem())
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
            }
        })

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_proceed -> {
                    actionProceed()
                    true
                }

                else -> false
            }
        }

    }

    private fun actionProceed() {
        _lineItem.apply { served_at = Date()}
        _lineItem.id?.let { viewModel.processLineItem(it,_lineItem) }
        dismiss()
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.clearForwardedReceipts()
        viewModel.releaseLineItem()
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


    private fun updateUI(lineItem: LineItem) {
        _lineItem = lineItem
        with(binding) {
            txtName.text = lineItem.item_name
            txtQte.text = "1"
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogRetrievalConfirmationBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

}