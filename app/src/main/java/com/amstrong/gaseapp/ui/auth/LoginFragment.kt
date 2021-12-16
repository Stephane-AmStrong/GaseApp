package com.amstrong.gaseapp.ui.auth

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.data.response.UserLoginDto
import com.amstrong.gaseapp.databinding.FragmentLoginBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.enable
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase


class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.progressBar.visible(false)
        binding.btnLogin.enable(false)


        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Failure -> handleApiError(it) { login() }
            }
        })


        binding.txtPin.addTextChangedListener {
            binding.btnLogin.enable(it.toString().isNotEmpty())
            if (binding.txtPin.text?.count()==4) login()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.txtLink2EmailLogin.setOnClickListener {
            toogleEmailPin()
        }

        binding.txtPin.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login()
                return@OnEditorActionListener true
            }
            false
        })

    }


    private fun login() {
        val email = binding.txtPin.text.toString().trim()
        viewModel.login(UserLoginDto(email))
    }

    private fun toogleEmailPin(){
        with(binding){
            //txtPin.setFilters(arrayOf<InputFilter>(LengthFilter(1)))
            if (lblPin.hint.toString()==resources.getString(R.string.enter_your_pin)){
                lblPin.setHint(resources.getString(R.string.enter_your_email))
                txtLink2EmailLogin.setText(resources.getString(R.string.use_pin))
                txtPin.setFilters(arrayOf<InputFilter>())
                txtPin.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }else{
                lblPin.setHint(resources.getString(R.string.enter_your_pin))
                txtLink2EmailLogin.setText(resources.getString(R.string.use_email))
                txtPin.setFilters(arrayOf<InputFilter>(LengthFilter(4)))
                txtPin.inputType = InputType.TYPE_CLASS_NUMBER
            }
            txtPin.setText("")
        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
        AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences, AppDatabase(requireContext()))
}