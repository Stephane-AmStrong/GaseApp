package com.amstrong.gaseapp.ui.charge

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentNewSaleBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.utils.sumByFloat
import com.amstrong.gaseapp.utils.toDecimalFormat
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class NewSaleFragment : BaseFragment<MainViewModel, FragmentNewSaleBinding, MainRepository>() {

    val args : NewSaleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }
                is Resource.Failure -> {

                }
            }
        })

        binding.btnSale.setOnClickListener {

            val navHostFragment = parentFragment as NavHostFragment?
            val parent: ChargeFragment? = navHostFragment!!.parentFragment as ChargeFragment?

            viewModel.truncateLineItemsFromRoom()
            parent?.navigateToWaiterFragment()
        }
    }


    private fun updateUI(receipt: Receipt?) {
        with(binding) {
            if (receipt != null){
                txtAmountPaid.text = args.amountReceived.toString().toFloat().toDecimalFormat()
                txtChange.text = (args.amountReceived.toFloat() - receipt.line_items.sumByFloat { it.price }).toDecimalFormat()
            }else{
                txtAmountPaid.text = "0"
                txtChange.text = "0"
            }
        }
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentNewSaleBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

}