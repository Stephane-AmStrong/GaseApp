package com.amstrong.gaseapp.ui.workstation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentWorkstationBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogRecordReceipt
import com.amstrong.gaseapp.ui.dialogs.DiablogMyReceipts
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WorkstationFragment : BaseFragment<MainViewModel, FragmentWorkstationBinding, MainRepository>() {

    protected lateinit var myId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getReceipts()
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentWorkstationBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}