package com.amstrong.gaseapp.ui.waiter

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentMyOrdersBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.formatToPattern
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class MyOrdersFragment : BaseFragment<MainViewModel, FragmentMyOrdersBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentMyOrdersBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}