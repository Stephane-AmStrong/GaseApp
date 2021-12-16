package com.amstrong.gaseapp.ui.dialogs

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.*
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.auth.MyProfileFragment
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class DiablogRegisterWorstation : BaseBottomSheet<MainViewModel, DiablogRegisterWorkstationBinding, MainRepository>() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private var workstationId: String? = null

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
                else -> false
            }
        }



        binding.progressBar.visible(false)
        clearToolbarMenu()

        viewModel.selectedWorkstation.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }

                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        viewModel.workstations.observe(viewLifecycleOwner, Observer {
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

        binding.txtName.addTextChangedListener {
            if (ready()) showToolbarMenu() else clearToolbarMenu()
        }
    }

    private fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    private fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.dialog_register
        )
    }

    private fun updateUI(workstation: Workstation?) {
        workstationId = workstation?.id

        with(binding) {

            if (workstationId != null) {
                Log.d(TAG, "onDismiss: workstation name ${workstation?.name}")

                txtName.setText(workstation!!.name)
                switchTakeOrders.isChecked = workstation.canTakeOrders
                switchServeDrinksOrPrepareMeals.isChecked = workstation.canPrepareMealsOrDrinks
                switchCashin.isChecked = workstation.canCashIn
                switchManagePurchaseOrder.isChecked = workstation.canManagePurchaseOrders
                switchManageHall.isChecked = workstation.canManageHall
                switchManageKitchen.isChecked = workstation.canManagekitchen
                switchManageUsers.isChecked = workstation.canManageUsers
                switchManageCategories.isChecked = workstation.canManageCategories
                switchManageWorkstations.isChecked = workstation.canManageWorkstations
            } else {
                txtName.setText("")
                switchTakeOrders.isChecked = false
                switchServeDrinksOrPrepareMeals.isChecked = false
                switchCashin.isChecked = false
                switchManagePurchaseOrder.isChecked = false
                switchManageHall.isChecked = false
                switchManageKitchen.isChecked = false
                switchManageUsers.isChecked = false
                switchManageCategories.isChecked = false
                switchManageWorkstations.isChecked = false
            }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.selectedWorkstation(null);
        super.onDismiss(dialog)
    }

    private fun ready(): Boolean {
        return !(binding.txtName.text.toString().isBlank())
    }

    private fun resetAndDismiss() {
        toast(binding.txtName.text.toString() + " enregistré")

        dismiss()
    }

    private fun saveUser() {
        with(binding) {
            if (txtName.text.toString().isEmpty()) {
                txtName.setError(resources.getString(R.string.error_workstation_name_required))
                return
            }

            val name = txtName.text.toString().trim()

            val switchTakeOrders = switchTakeOrders.isChecked
            val switchServeDrinksOrPrepareMeals = switchServeDrinksOrPrepareMeals.isChecked
            val switchCashIn = switchCashin.isChecked
            val switchManagePurchaseOrder = switchManagePurchaseOrder.isChecked
            val switchManageHall = switchManageHall.isChecked
            val switchManagekitchen = switchManageKitchen.isChecked
            val switchManageUsers = switchManageUsers.isChecked
            val switchManageCategories = switchManageCategories.isChecked
            val switchManageWorkstations = switchManageWorkstations.isChecked

            viewModel.registerWorkstation(workstationId, Workstation("guid", name, switchTakeOrders, switchServeDrinksOrPrepareMeals, switchCashIn, switchManagePurchaseOrder, switchManageHall, switchManagekitchen, switchManageUsers, switchManageCategories, switchManageWorkstations, Date()))
        }

    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogRegisterWorkstationBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }
}