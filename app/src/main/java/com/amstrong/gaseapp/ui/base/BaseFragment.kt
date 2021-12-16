package com.amstrong.gaseapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.RemoteDataSource
import com.amstrong.gaseapp.data.repositories.BaseRepository
import com.amstrong.gaseapp.ui.auth.AuthActivity
import com.amstrong.gaseapp.utils.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


abstract class BaseFragment<VM : BaseViewModel, B : ViewBinding, R : BaseRepository> : Fragment() {

    protected lateinit var userPreferences: UserPreferences
    protected lateinit var binding: B
    protected lateinit var viewModel: VM
    protected val remoteDataSource = RemoteDataSource()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userPreferences = UserPreferences(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
//        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        viewModel = ViewModelProvider(requireActivity(), factory).get(getViewModel())

        lifecycleScope.launch { userPreferences.authToken.first() }
        //
        setHasOptionsMenu(true)

        //
        return binding.root
    }

    fun logout() = lifecycleScope.launch{
        val authToken = userPreferences.authToken.first()
        val api = remoteDataSource.buildApi(AuthApi::class.java, authToken)
        viewModel.logout(api)
        userPreferences.clearEverything()
        requireActivity().startNewActivity(AuthActivity::class.java)
    }

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getFragmentRepository(): R

}