package com.amstrong.gaseapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentMyProfilBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class MyProfileFragment : BaseFragment<MainViewModel, FragmentMyProfilBinding, MainRepository>(){

    lateinit var _user: Employee

    companion object {
        fun newInstance() = MyProfileFragment()
    }override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.progressBar.visible(false)
        binding.btnRegister.enable(false)

        viewModel.user.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    updateUI(it.value)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        binding.txtName.addTextChangedListener {
            binding.btnRegister.enable(ready())
        }

        binding.txtEmail.addTextChangedListener {
            binding.btnRegister.enable(ready())
        }

        binding.txtOpenId.addTextChangedListener {
            binding.btnRegister.enable(ready())
        }

        binding.btnRegister.setOnClickListener {
            register()
        }
    }


    private fun updateUI(user: Employee) {
        _user = user
        with(binding) {
            txtName.setText(user.name)
            txtPhoneNumber.setText(user.phone_number)
            txtEmail.setText(user.email)

            if (user.open_id.count()==4) txtOpenId.setText(user.open_id) else txtOpenId.setText("")
        }
    }

    private fun ready() : Boolean {
        return !(binding.txtName.text.toString().isBlank() || binding.txtEmail.text.toString().isBlank() || binding.txtOpenId.text.toString().isBlank() || binding.txtOpenId.text.toString().count()<4)
    }

    private fun register() {
        _user.apply {
            name = binding.txtName.text.toString().trim()
            phone_number = binding.txtPhoneNumber.text.toString().trim()
            email = binding.txtEmail.text.toString().trim()
            open_id = binding.txtOpenId.text.toString().trim()
            updated_at = Date()
        }

        viewModel.registerUser(_user.id, _user)
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentMyProfilBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}