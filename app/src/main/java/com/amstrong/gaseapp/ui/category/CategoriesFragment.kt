package com.amstrong.gaseapp.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amstrong.gaseapp.data.db.entities.Category
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentCategoriesBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.ui.dialogs.DiablogRegisterCategory
import com.amstrong.gaseapp.ui.recycler_rows.CategoryRow
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.OnItemLongClickListener
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class CategoriesFragment : BaseFragment<MainViewModel, FragmentCategoriesBinding, MainRepository>() {
// var _actionMode: ActionMode?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.include.progressBar.visible(false)

        viewModel.getWorkstations()
        viewModel.getAllCategories()

        viewModel.allCategories.observe(viewLifecycleOwner, Observer {
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
    }

    private fun updateUI(categories: List<Category>) {

//        for(item : Item in items.filter { it.category_id == categoryId }){
//            variants.addAll(item.variants)
//        }

        with(binding) {
            val mAdapter = GroupAdapter<ViewHolder>().apply {
                addAll(categories.toCategoryRow())
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
    ) = FragmentCategoriesBinding.inflate(inflater, container, false)


    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }


    private val onItemClickListener = OnItemClickListener { row, view ->
        row as CategoryRow
        viewModel.selectedCategory(row.category)

        parentFragment?.let { parent -> DiablogRegisterCategory().show(parent.childFragmentManager, tag) }

    }

    private val onItemLongClickListener = OnItemLongClickListener { item, _ ->
//        if (item is CardItem && !item.text.isNullOrBlank()) {
//            Toast.makeText(this@MainActivity, "Long clicked: " + item.text, Toast.LENGTH_SHORT).show()
//            return@OnItemLongClickListener true
//        }
        false
    }

    private fun List<Category>.toCategoryRow(): List<CategoryRow> {
        return this.map {
            CategoryRow(it)
        }
    }
}