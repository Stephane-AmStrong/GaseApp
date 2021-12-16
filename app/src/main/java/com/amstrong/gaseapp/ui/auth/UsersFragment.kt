package com.amstrong.gaseapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.databinding.FragmentUsersBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogRegisterUser
import com.amstrong.gaseapp.ui.recycler_rows.UserRow
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UsersFragment : BaseFragment<AuthViewModel, FragmentUsersBinding, AuthRepository>() {
// var _actionMode: ActionMode?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include.progressBar.visible(false)

        viewModel.getWorkstations()
        viewModel.getEmployees()

        viewModel.employees.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)
                    updateUI(it.value)
                }
                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }
                is Resource.Failure -> handleApiError(it) {

                }
            }
        })

        binding.fab.setOnClickListener (onClickListener)
    }

    private fun updateUI(users: List<Employee>) {

//        for(item : Item in items.filter { it.category_id == categoryId }){
//            variants.addAll(item.variants)
//        }

        with(binding) {
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(users.toUserRow())
                setOnItemClickListener(onItemClickListener)

            }

            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter

            }


        }
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUsersBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): AuthRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(AuthApi::class.java, token)
        val db = AppDatabase(requireContext())
        return AuthRepository(api, userPreferences,db)
    }


    private val onItemClickListener = OnItemClickListener { userRow, view ->
        userRow as UserRow

//        var userReadDto = Employee(
//            userRow.user.id,
//            userRow.user.name,
//            userRow.user.email,
//            userRow.user.phone_number,
//            userRow.user.workstation_id,
//        )
        viewModel.selectEmployee(userRow.user)

        parentFragment?.let { parent -> DiablogRegisterUser().show(parent.childFragmentManager, tag) }
    }

    private val onItemLongClickListener = OnItemLongClickListener { item, _ ->
//        if (item is CardItem && !item.text.isNullOrBlank()) {
//            Toast.makeText(this@MainActivity, "Long clicked: " + item.text, Toast.LENGTH_SHORT).show()
//            return@OnItemLongClickListener true
//        }
        false
    }

    private fun List<Employee>.toUserRow(): List<UserRow> {
        return this.map {
            UserRow(it)
        }
    }


    private val onClickListener = View.OnClickListener {
        val mainActivity = requireActivity()
        mainActivity as MainActivity

        when(it.id){
            R.id.fab -> {
                parentFragment?.let { parent -> DiablogRegisterUser().show(parent.childFragmentManager, tag) }
                true
            }


            else -> false
        }
    }
}