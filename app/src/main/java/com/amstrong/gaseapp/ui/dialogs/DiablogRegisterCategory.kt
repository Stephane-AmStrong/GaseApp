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
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.*
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.adapters.SpinWorkstationAdapter
import com.amstrong.gaseapp.ui.auth.MyProfileFragment
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DiablogRegisterCategory : BaseBottomSheet<MainViewModel, DiablogRegisterCategoryBinding, MainRepository>() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }
    private lateinit var workstations: List<Workstation>
    private var category: Category? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.toolbar.setTitle(R.string.category_edition)

        binding.btnClose.setOnClickListener {
            dismiss()
        }


        binding.toolbar.inflateMenu(R.menu.dialog_register)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_register -> {
                    saveCategory()
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


        viewModel.selectedCategory.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {

                }
                is Resource.Loading -> {
                    dismiss()
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })



        binding.txtName.addTextChangedListener {
            if (ready()) showToolbarMenu() else clearToolbarMenu()
        }

    }


    fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.dialog_register
        )
    }

    private fun updateUI(category: Category?) {
        this.category = category
        Log.d(ContentValues.TAG, "onDismiss: updateUI")

        with(binding){
            if (category!=null){
                txtName.setText(category.name)
                swtAvailable4Sale.isChecked = category.available_for_sale
                spinWorkstation.setSelection(workstations.indexOf(category.workstation))
            }else{
                txtName.setText("")
                swtAvailable4Sale.isChecked = false
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.selectedCategory(null);
        super.onDismiss(dialog)
    }

    private fun loadSpinner(workstations: List<Workstation>) {
        this.workstations = workstations
        val dataAdapter = SpinWorkstationAdapter(requireContext(), R.layout.simple_spinner_item, workstations)
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_drop_down)

        binding.spinWorkstation.setAdapter(dataAdapter);
        binding.spinWorkstation.onItemSelectedListener = onItemSelectedListener
    }

    private fun ready() : Boolean {
        return !(binding.txtName.text.toString().isBlank())
    }

    private fun saveCategory() {
        if (binding.txtName.text.toString().isEmpty()){
            binding.txtName.setError(resources.getString(R.string.error_category_name_required))
            return
        }
        val workstation = workstations[binding.spinWorkstation.selectedItemPosition]
        val available4Sale = binding.swtAvailable4Sale.isChecked

        category?.available_for_sale = available4Sale
        category?.workstation_id = workstation.id

        if (category!=null){
            viewModel.saveCategory(category!!.id, category!!)
        }else{
            toast(resources.getString(R.string.error_select_category))
        }

    }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//            if(parent?.getItemAtPosition(position)?.equals("")){
//
//            }else{
//
//            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogRegisterCategoryBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}