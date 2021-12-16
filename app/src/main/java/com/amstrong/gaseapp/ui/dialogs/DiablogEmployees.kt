package com.amstrong.gaseapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.DiablogEmployeesBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseBottomSheet
import com.amstrong.gaseapp.ui.recycler_rows.EmployeeRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class DiablogEmployees : BaseBottomSheet<MainViewModel, DiablogEmployeesBinding, MainRepository>(){

    private lateinit var mAdapter: GroupAdapter<ViewHolder>
    private var _employees =  listOf<Employee>()
    private lateinit var lineItem: LineItem
    private lateinit var workstation: Workstation
    private var selectedReceiptIds = listOf<String>()

    var waitersOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        waitersOnly = arguments?.getBoolean(ARG_WAITERS_ONLY)!!;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.toolbar.setTitle(R.string.selectionner_employe)

        binding.include.progressBar.visible(false)

        viewModel.workstation.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)
                    updateUI(it.value.employees)
                    workstation = it.value
                }
                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }
            }
        })

        viewModel.lineItemWithTaxes.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    if(it.value!=null){
                        binding.include.progressBar.visible(false)
                        lineItem = it.value.lineItem.copy()
                    }
                }
                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }
            }
        })

        viewModel.receipts.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {

                }
                is Resource.Loading -> {
                    resetAndDismiss()
                }
            }
        })

        viewModel.users.observe(viewLifecycleOwner, {
            binding.include.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    _employees = it.value
                    updateUI(_employees)
                }

                is Resource.Failure -> {
                    toast(resources.getString(R.string.error_loading_users))
                }
            }
        })

        viewModel.forwardedReceipts.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    resetAndDismiss()
                }
                is Resource.Loading -> {

                }
            }
        })

        viewModel.selectedReceiptIds.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)

                    selectedReceiptIds = (it.value)
                }

                is Resource.Failure -> {
                    toast(resources.getString(R.string.error_loading_tickets)+" sélectionnés")
                }
            }
        })


        binding.btnClose.setOnClickListener {
            dismiss()
        }

        clearToolbarMenu()
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_proceed -> {
                    if (waitersOnly) {
                        viewModel.forwardReceipts(_employees.first{ it.isSelected }.id, selectedReceiptIds)
                    } else {
                        lineItem.apply {
                            caterer_id = _employees.first{ it.isSelected }.id
                            ready_at = Date()
                        }

                        lineItem.id?.let { viewModel.processLineItem(it, lineItem) }
                    }

                    true
                }

                else -> false
            }
        }

    }

    override fun dismiss() {
        super.dismiss()
        viewModel.clearForwardedReceipts()
        viewModel.releaseLineItem()
    }


    fun clearToolbarMenu() {
        binding.toolbar.menu.clear()
    }

    fun showToolbarMenu() {
        clearToolbarMenu()
        binding.toolbar.inflateMenu(
                R.menu.proceed
        )
    }


    private fun updateUI(employees: List<Employee>) {
        with(binding) {

            _employees = if (waitersOnly) employees.filter { it.workstation.canTakeOrders } else employees


            mAdapter = GroupAdapter<ViewHolder>().apply {
                if (waitersOnly) addAll(employees.filter { it.workstation.canTakeOrders }.toEmployeeRow()) else addAll(employees.toEmployeeRow())
                setOnItemClickListener(onItemClickListener)
            }


            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }


    private fun resetAndDismiss() {
        toast(" enregistré")
        dismiss()
    }


    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = DiablogEmployeesBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

    private val onItemClickListener = OnItemClickListener { employeeRow, view ->
        employeeRow as EmployeeRow
        //employeeRow.cardView.isSelected = true
        _employees.map { it.isSelected = false }

        employeeRow.employee.isSelected = true

        if (_employees.any { it.isSelected }) {
            showToolbarMenu()
        } else {
            clearToolbarMenu()
        }

        mAdapter.notifyDataSetChanged()
    }

    private fun List<Employee>.toEmployeeRow(): List<EmployeeRow> {
        return this.map {
            EmployeeRow(it)
        }
    }


    companion object {
        private const val ARG_WAITERS_ONLY = "arg_waiters_only"

        @JvmStatic
        fun newInstance(waitersOnly: Boolean): DiablogEmployees {
            return DiablogEmployees().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_WAITERS_ONLY, waitersOnly)
                }
            }
        }
    }

}