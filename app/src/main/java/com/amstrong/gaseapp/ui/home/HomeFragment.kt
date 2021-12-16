package com.amstrong.gaseapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentHomeBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.auth.AuthActivity
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.startNewActivity
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeFragment :
        BaseFragment<MainViewModel, FragmentHomeBinding, MainRepository>() {

    protected lateinit var myWorkstationId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val userPreferences = UserPreferences(requireContext())
//
//        userPreferences.workstation_id.asLiveData().observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                viewModel.getWorkstation(it)
//            }
//        })

        viewModel.getWorkstation(myWorkstationId)

        viewModel.workstation.observe(viewLifecycleOwner, Observer {

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }

                is Resource.Failure -> handleApiError(it) {
                    //viewModel.getVariants()
                }
            }
        })


    }

    private fun updateUI(workstation: Workstation) {

        with(binding) {
//            txtWorkstation.setText(workstation.name)
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        myWorkstationId = runBlocking { userPreferences.workstation_id.first()!! }
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

}