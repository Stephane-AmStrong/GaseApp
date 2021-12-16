package com.amstrong.gaseapp.ui.waiter

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentAuMenuBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.recycler_rows.VariantRow
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.runningOnTablet
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*

class AuMenuFragment : BaseFragment<MainViewModel, FragmentAuMenuBinding, MainRepository>() {

    var categoryId: String? = null
    var canTakeOrders: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryId = arguments?.getString(ARG_CATEGORY_ID);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include.progressBar.visible(false)

        viewModel.allItems.observe(viewLifecycleOwner, Observer {
            binding.include.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.include.progressBar.visible(false)
                    updateUI(it.value.filter { it.category_id == this.categoryId })
                }

                is Resource.Loading -> {
                    binding.include.progressBar.visible(true)
                }

                is Resource.Failure -> handleApiError(it) {
//                    viewModel.getVariants()
                }
            }
        })
    }

    companion object {
        private const val ARG_CATEGORY_ID = "arg_category_id"

        @JvmStatic
        fun newInstance(categoryId: String): AuMenuFragment {
            return AuMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_ID, categoryId)
                }
            }
        }
    }

    private fun updateUI(items: List<Item>) {
        var variants = arrayListOf<Variant>()

        for (item: Item in items) {
//            item.variants.map { it.image_url = item.image_url }
//            item.variants.map { it.item_name = item.item_name }
            item.variants.map { it.color = item.color }
            variants.addAll(item.variants)
        }

        with(binding) {
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(variants.toVariantRow())
                setOnItemClickListener(onItemClickListener)

            }

            include.recyclerView.apply {
                layoutManager = GridLayoutManager(context, requireContext().resources.getInteger(R.integer.number_of_grid_items))
                setHasFixedSize(true)
                adapter = mAdapter

            }


        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ) = FragmentAuMenuBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        canTakeOrders = runBlocking { userPreferences.canTakeOrders.first()!! }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private val onItemClickListener = OnItemClickListener { row, view ->
        row as VariantRow

        var lineTaxes = arrayListOf<LineTax>()
        for (tax_id: String in row.variant.tax_ids) {
            lineTaxes.add(LineTax(null, tax_id))
        }


//        for(lineTaxe in lineTaxes){
//
//            Log.d(ContentValues.TAG, "lineItemsWithTaxes: lineTaxes.count " + lineTaxes.count())
//        }


        var lineItem = LineItem(
                null,
                null,
                row.variant.item_id,
                row.variant.item_name,
                row.variant.image_url,
                row.variant.workstation_id,
                row.variant.variant_id,
                row.variant.variant_name,
                1F,
                // gase Store id
//            row.food.stores.filter { it.store_id=="faa633d3-711d-11ea-8d93-0603130a05b8" && it.variant_id == row.food.variant_id }.first().price,
                //test store id
//            row.food.stores.filter { it.store_id=="f3c8b5b0-b8de-4ad4-b4b2-46bfa5288bf8" && it.variant_id == row.food.variant_id }.first().price,
                row.variant.stores.filter { it.variant_id == row.variant.variant_id }.first().price,
                row.variant.cost,
                null,
                null,
                Date(),
                null,
                null,
                false,
                false,
                lineTaxes,
        )

        if (canTakeOrders) {

            if (row.variant.in_stock > 0) {
                viewModel.saveLineItemsToRoom(lineItem)
            } else {
                toast(getString(R.string.error_item_unavailable, row.variant.item_name))
            }
        }
    }

    private val onItemLongClickListener = OnItemLongClickListener { item, _ ->
//        if (item is CardItem && !item.text.isNullOrBlank()) {
//            Toast.makeText(this@MainActivity, "Long clicked: " + item.text, Toast.LENGTH_SHORT).show()
//            return@OnItemLongClickListener true
//        }
        false
    }

    private fun List<Variant>.toVariantRow(): List<VariantRow> {
        return this.map {
            VariantRow(it)
        }
    }
}