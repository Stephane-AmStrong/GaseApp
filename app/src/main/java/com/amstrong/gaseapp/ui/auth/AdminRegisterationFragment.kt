package com.amstrong.gaseapp.ui.auth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.data.response.UserCreateDto
import com.amstrong.gaseapp.databinding.FragmentAdminRegistrationBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AdminRegisterationFragment : BaseFragment<AuthViewModel, FragmentAdminRegistrationBinding, AuthRepository>(){

    private lateinit var _workstation: Workstation
    lateinit var _user: Employee

    companion object {
        fun newInstance() = AdminRegisterationFragment()
    }override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.progressBar.visible(false)
        binding.btnRegister.enable(false)


        viewModel.workstations.observe(viewLifecycleOwner, Observer {
            when(it){

                is Resource.Success ->{
                    updateUIWorkstations(it.value)
                }

                is Resource.Failure ->{

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
            saveUser()
        }
    }


    private fun saveUser() {

        with(binding){
            if (txtName.text.toString().isEmpty()){
                txtName.setError(resources.getString(R.string.error_user_name_required))
                return
            }

            if (txtEmail.text.toString().isEmpty()){
                txtEmail.setError(resources.getString(R.string.error_email_required))
                return
            }

            if (txtOpenId.text.toString().isEmpty()){
                txtOpenId.setError(resources.getString(R.string.error_field_required))
                return
            }

            if (txtOpenIdConfirmed.text.toString()!= txtOpenId.text.toString()){
                txtOpenId.setError(resources.getString(R.string.error_pin_mismatch))
                txtOpenIdConfirmed.setError(resources.getString(R.string.error_pin_mismatch))
                return
            }

        }


        val name = binding.txtName.text.toString().trim()
        val phone = binding.txtPhoneNumber.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()
        val openID = binding.txtOpenId.text.toString().trim()

        viewModel.registerUser(null, UserCreateDto(name,_workstation.id!!, email, phone,  openID,null,null))
        viewModel.getEmployeesCount()
    }


    private fun updateUIWorkstation(workstation: Workstation){
        try {
            //_workstation = workstations.filter { it.name == resources.getString(R.string.administrator_workstation) }.first()
        }catch (ex : Exception){
            Log.d(TAG, "updateUIWorkstationsException : ${ex}")
        }

        _workstation = workstation
    }

    private fun updateUIWorkstations(workstations: List<Workstation>){
        try {
            _workstation = workstations.filter { it.name == resources.getString(R.string.administrator_workstation) }.first()
        }catch (ex : Exception){
            Log.d(TAG, "updateUIWorkstationsException : ${ex}")
        }
    }



    private fun ready() : Boolean {
        return !(binding.txtName.text.toString().isBlank() || binding.txtEmail.text.toString().isBlank() || binding.txtOpenId.text.toString().isBlank() || binding.txtOpenId.text.toString().count()<4)
    }


    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentAdminRegistrationBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): AuthRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(AuthApi::class.java, token)
        val db = AppDatabase(requireContext())
        return AuthRepository(api, userPreferences, db)
    }
}