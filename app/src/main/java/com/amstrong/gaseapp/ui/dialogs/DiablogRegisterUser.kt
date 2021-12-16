package com.amstrong.gaseapp.ui.dialogs

import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.data.response.UserCreateDto
import com.amstrong.gaseapp.databinding.*
import com.amstrong.gaseapp.ui.adapters.SpinWorkstationAdapter
import com.amstrong.gaseapp.ui.auth.AuthViewModel
import com.amstrong.gaseapp.ui.auth.MyProfileFragment
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import java.util.*

class DiablogRegisterUser : BaseBottomSheet<AuthViewModel, DiablogRegisterUserBinding, AuthRepository>() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }
    private lateinit var workstations: List<Workstation>
    private var employeeId: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.setTitle(R.string.workstation_creation)

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.toolbar.inflateMenu(R.menu.dialog_register)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_register -> {
                    saveUser()
                    true
                }

                R.id.action_delete_user -> {
                    toast("la suppression de compte est désactivée pour le moment")
                    true
                }

                else -> false
            }
        }


        binding.progressBar.visible(false)
        clearToolbarMenu()

        viewModel.workstations.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {

                    loadSpinner(it.value)
                }

                is Resource.Loading -> {
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.selectedEmployee.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })


        viewModel.employees.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {

                }
                is Resource.Loading -> {
                    resetAndDismiss()
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        binding.txtName.addTextChangedListener {
            if (ready()) showToolbarMenu() else clearToolbarMenu()
        }

        binding.txtEmail.addTextChangedListener {
            if (ready()) showToolbarMenu() else clearToolbarMenu()
        }

        binding.btnResetPin.setOnClickListener {
            resetPin()
        }
    }

    private fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    private fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.dialog_user
        )
    }


    private fun updateUI(employee: Employee?) {
        employeeId = employee?.id

        with(binding){
            txtName.setText(employee?.name)
            txtPhoneNumber.setText(employee?.phone_number)
            txtEmail.setText(employee?.email)
            switchEnable.isChecked = employee?.disabled_at == null
            if (employee!=null) showToolbarMenu() else clearToolbarMenu()
            spinWorkstation.setSelection(workstations.indexOf(employee?.workstation))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.selectEmployee(null);
        super.onDismiss(dialog)
    }

    private fun loadSpinner(workstations: List<Workstation>) {
//        val dataAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, workstations)
        this.workstations = workstations
        val dataAdapter = SpinWorkstationAdapter(requireContext(), R.layout.simple_spinner_item, workstations)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down)

        binding.spinWorkstation.setAdapter(dataAdapter);
        binding.spinWorkstation.onItemSelectedListener = onItemSelectedListener
    }

    private fun ready() : Boolean {
        return !(binding.txtName.text.toString().isBlank() && binding.txtEmail.text.toString().isBlank())
    }

    private fun resetAndDismiss(){
        toast( binding.txtName.text.toString()+ " enregistré")
        clearToolbarMenu()
        binding.txtName.setText("")
        binding.txtPhoneNumber.setText("")
        binding.txtEmail.setText("")

        viewModel.selectEmployee(null);
        dismiss()
    }

    private fun saveUser() {
        if (binding.txtName.text.toString().isEmpty()){
            binding.txtName.setError(resources.getString(R.string.error_user_name_required))
            return
        }

        if (binding.txtEmail.text.toString().isEmpty()){
            binding.txtEmail.setError(resources.getString(R.string.error_email_required))
            return
        }

        val disabled_at: Date? = if(binding.switchEnable.isChecked) null else Date()

        val name = binding.txtName.text.toString().trim()
        val phone = binding.txtPhoneNumber.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()
        val workstation = workstations[binding.spinWorkstation.selectedItemPosition]

        viewModel.registerUser(employeeId, UserCreateDto(name,workstation.id!!, email, phone,  email, Date(), disabled_at))
    }

    private fun resetPin() {
        if (binding.txtName.text.toString().isEmpty()){
            binding.txtName.setError(resources.getString(R.string.error_user_name_required))
            return
        }

        if (binding.txtEmail.text.toString().isEmpty()){
            binding.txtEmail.setError(resources.getString(R.string.error_email_required))
            return
        }

        val disabled_at: Date? = if(binding.switchEnable.isChecked) null else Date()

        val name = binding.txtName.text.toString().trim()
        val phone = binding.txtPhoneNumber.text.toString().trim()
        val email = binding.txtEmail.text.toString().trim()
        val workstation = workstations[binding.spinWorkstation.selectedItemPosition]

        viewModel.registerUser(employeeId, UserCreateDto(name,workstation.id!!, email, phone,  email, Date(), disabled_at))
    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogRegisterUserBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() =
            AuthRepository(remoteDataSource.buildApi(AuthApi::class.java), userPreferences, AppDatabase(requireContext()))
}