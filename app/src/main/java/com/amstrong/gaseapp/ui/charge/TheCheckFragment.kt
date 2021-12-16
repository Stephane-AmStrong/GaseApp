package com.amstrong.gaseapp.ui.charge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentTheCheckBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import androidx.navigation.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.util.toast
import kotlinx.coroutines.runBlocking

class TheCheckFragment :
    BaseFragment<MainViewModel, FragmentTheCheckBinding, MainRepository>() {
    private lateinit var receipt : Receipt
    protected var canCashIn: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if (it.value!=null) receipt = it.value
                }
            }
        })

        binding.btnValidate4Printing.setOnClickListener {
            if (receipt.line_items.all { it.caterer_id != null }) {
                viewModel.closeReceipt(receipt.id)

                if (canCashIn) {
                    val action = TheCheckFragmentDirections.navigateToChargeFragment()
                    it.findNavController().navigate(action)
                } else {
                    viewModel.truncateLineItemsFromRoom()
                    val action = TheCheckFragmentDirections.navigateToWaiterFragment()
                    it.findNavController().navigate(action)
                }
            } else {
                toast(resources.getString(R.string.there_are_pending_dishes))
            }
        }


    }



    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTheCheckBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        canCashIn = runBlocking { userPreferences.canCashIn.first()!! }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}