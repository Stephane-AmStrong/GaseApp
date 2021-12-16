package com.amstrong.gaseapp.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.Order
import com.amstrong.gaseapp.data.db.entities.OrderLine
import com.amstrong.gaseapp.data.db.entities.Store
import com.amstrong.gaseapp.data.db.entities.Supplier
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentOrdersBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.recycler_rows.OrderRow
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class OrdersFragment : BaseFragment<MainViewModel, FragmentOrdersBinding, MainRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.include.progressBar.visible(false)

        viewModel.getSuppliers()
        viewModel.getStores()
        viewModel.getOrders()

        viewModel.orders.observe(viewLifecycleOwner, Observer {
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

    private fun updateUI(orders: List<Order>) {

        with(binding) {
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(orders.toUserRow())
                setOnItemClickListener(onItemClickListener)

            }

            include.recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = mAdapter

            }
        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentOrdersBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private val onItemClickListener = OnItemClickListener { row, view ->
        row as OrderRow

        viewModel.getOrder(row.order.id!!)

        val action = OrdersFragmentDirections.navigateToRegisterOrderFragment()
        findNavController().navigate(action)
    }

    private val onItemLongClickListener = OnItemLongClickListener { item, _ ->

        false
    }

    private fun List<Order>.toUserRow(): List<OrderRow> {
        return this.map {
            OrderRow(it)
        }
    }


    private val onClickListener = View.OnClickListener {
        val mainActivity = requireActivity()
        mainActivity as MainActivity

        when(it.id){
            R.id.fab -> {
                viewModel.setOrder(Order(arrayListOf<OrderLine>()))

                val action = OrdersFragmentDirections.navigateToRegisterOrderFragment()
                it.findNavController().navigate(action)

                true
            }


            else -> false
        }
    }
}