package com.amstrong.gaseapp.ui.charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentReceiptBinding
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.waiter.AuMenuFragment
import com.amstrong.gaseapp.ui.recycler_rows.LineItemWithTaxesRow
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.utils.*
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ReceiptFragment : BaseFragment<MainViewModel, FragmentReceiptBinding, MainRepository>() {

    var forEdition: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forEdition = arguments?.getBoolean(ARG_FOR_EDITION);
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (forEdition == true) {
            viewModel.getLineItemWithTaxesFromRoom()
        } else {
            binding.txtDiningOption.enable(false)
        }

        binding.rowTotal.getRoot().visible(false)
        viewModel.receipt.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)

                    //disable spinner
                    if (forEdition == false && it.value!=null) {
                        binding.txtDiningOption.setSelection(resources.getStringArray(R.array.dining_option).indexOf(it.value.dining_option))
                    }
                }


            }
        })


    }

    private fun updateUI(receipt: Receipt?) {
        if(receipt!=null){
            val groupLines = arrayListOf<LineItemWithTaxes>()
            val distinctLines = receipt.line_items.distinctBy { it.variant_id }

            for (line: LineItem in distinctLines) {
                //groupLines.add(line.copy(quantity = (receipt.line_items.count { it.variant_id == line.variant_id }).toFloat()))
                groupLines.add(line.toLineItemWithTaxes().apply { qte = (receipt.line_items.count { it.variant_id == line.variant_id }).toInt()})
            }

            with(binding) {
                if (receipt.line_items.any()) rowTotal.getRoot().visible(true) else rowTotal.getRoot().visible(false)
                rowTotal.txtName.text = "Total"
                rowTotal.txtQte.visible(false)
                rowTotal.txtAmount.text = receipt.line_items.sumByFloat { it.price }.toDecimalFormat()

                val mAdapter = GroupAdapter<ViewHolder>().apply {
                    addAll(groupLines.toLineItemWithTaxesRow())
//                addAll(lineItems.toOrderLineRow())
                    setOnItemClickListener(onItemClickListener)
                }

                include.recyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    adapter = mAdapter
                }
            }
        }else{

        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentReceiptBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val apiMenu = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(apiMenu, db)
    }

    private val onItemClickListener = OnItemClickListener { item, view ->

    }

    private fun List<LineItemWithTaxes>.toLineItemWithTaxesRow(): List<LineItemWithTaxesRow> {
        return this.map {
            LineItemWithTaxesRow(it)
        }
    }

    companion object {
        private const val ARG_FOR_EDITION = "arg_for_edition"

        @JvmStatic
        fun newInstance(forEdition: Boolean): AuMenuFragment {
            return AuMenuFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_FOR_EDITION, forEdition)
                }
            }
        }
    }

}