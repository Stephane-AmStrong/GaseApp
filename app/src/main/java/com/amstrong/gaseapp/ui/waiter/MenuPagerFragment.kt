package com.amstrong.gaseapp.ui.waiter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amstrong.gaseapp.data.db.entities.Category
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentMenuPagerBinding
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.ui.base.BaseFragment
import com.amstrong.gaseapp.utils.handleApiError
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MenuPagerFragment :
    BaseFragment<MainViewModel, FragmentMenuPagerBinding, MainRepository>() {

    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tabTitles: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCategoriesAvailable4Sale()
        viewModel.getAllItems()

        viewModel.categoriesAvailable4Sale.observe(viewLifecycleOwner, Observer {

            when (it) {
                is Resource.Success -> {
                    updateUI(it.value)
                }

                is Resource.Failure -> handleApiError(it)
            }
        })



    }

    private fun updateUI(categories: List<Category>) {

        tabTitles = arrayListOf()
        fragments = arrayListOf()

        for (category: Category in categories) {
            fragments.add(AuMenuFragment.newInstance(category.id))
            tabTitles.add(category.name)
        }

        with(binding) {

            val sectionsPagerAdapter =
                    SectionsPagerAdapter(requireContext(), childFragmentManager, tabTitles, fragments)
            viewPager.adapter = sectionsPagerAdapter
            tabLayout.setupWithViewPager(binding.viewPager)

        }
    }

    override fun getViewModel() = MainViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMenuPagerBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDataSource.buildApi(MainApi::class.java, token)
        val db = AppDatabase(requireContext())
        return MainRepository(api, db)
    }

}