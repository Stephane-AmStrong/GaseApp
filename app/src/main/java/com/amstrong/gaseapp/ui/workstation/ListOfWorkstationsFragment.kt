package com.amstrong.gaseapp.ui.workstation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentListOfWorkstationsBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogMyReceipts
import com.amstrong.gaseapp.ui.dialogs.DiablogRecordReceipt
import com.amstrong.gaseapp.ui.dialogs.DiablogRegisterWorstation
import com.amstrong.gaseapp.ui.recycler_rows.WorkstationRow
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ListOfWorkstationsFragment : BaseFragment<MainViewModel, FragmentListOfWorkstationsBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getWorkstations()

        viewModel.workstations.observe(viewLifecycleOwner, {
            binding.include.progressBar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    updateUI(it.value)

                }
            }
        })

        binding.fab.setOnClickListener (onClickListener)

    }


    private fun updateUI(workstations: List<Workstation>) {


        with(binding) {
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(workstations.toWorkstationRow())
                setOnItemClickListener(onItemClickListener)
            }

            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter
            }
        }
    }


    private val onItemClickListener = OnItemClickListener { workstationRow, view ->
        workstationRow as WorkstationRow

        viewModel.selectedWorkstation(workstationRow.workstation)

        parentFragment?.let { parent -> DiablogRegisterWorstation().show(parent.childFragmentManager, tag) }

    }


    private val onClickListener = View.OnClickListener {
        val mainActivity = requireActivity()
        mainActivity as MainActivity

        when(it.id){
            R.id.fab -> {
                viewModel.selectedWorkstation(null)
                parentFragment?.let { parent -> DiablogRegisterWorstation().show(parent.childFragmentManager, tag) }
                true
            }


            else -> false
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentListOfWorkstationsBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private fun List<Workstation>.toWorkstationRow(): List<WorkstationRow> {
        return this.map {
            WorkstationRow(it)
        }
    }
}