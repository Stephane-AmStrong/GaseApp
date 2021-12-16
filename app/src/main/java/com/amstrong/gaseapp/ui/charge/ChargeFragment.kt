package com.amstrong.gaseapp.ui.charge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentChargeBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChargeFragment : BaseFragment<MainViewModel, FragmentChargeBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    //myReceiptId = it.value.id
                }
                is Resource.Loading -> {

                }

            }
        })

        userPreferences.authToken.asLiveData().observe(viewLifecycleOwner, Observer {
            if (it == null) {
                //startNewActivity(AuthActivity::class.java)
            } else {
                userPreferences.workstation_id.asLiveData().observe(viewLifecycleOwner, Observer {
                    //binding.btnPay.visible((it == "44444444-4444-4444-4444-444444444444"))
                })
            }

        })


//        binding.btnPay.setOnClickListener(View.OnClickListener {
//            Intent(this, ChargeActivity::class.java).apply {
//                putExtra(EXTRA_RECEIPT_ID, intent.getStringExtra(EXTRA_RECEIPT_ID))
//            }.also {
//                startActivity(it)
//            }
//            val action = ChargeFragmentDirections.navigateToPayReceiptFragment()
//            it.findNavController().navigate(action)
//        })



    }

    fun navigateToWaiterFragment(){

        val action = ChargeFragmentDirections.navigatebackToWaiterFragment()
        findNavController().navigate(action)
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentChargeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api,db)
    }
}