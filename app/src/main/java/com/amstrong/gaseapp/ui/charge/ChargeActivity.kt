package com.amstrong.gaseapp.ui.charge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.network.RemoteDataSource
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.FragmentChargeBinding
import com.amstrong.gaseapp.ui.base.ViewModelFactory
import com.amstrong.gaseapp.ui.MainViewModel
import com.amstrong.gaseapp.utils.EXTRA_RECEIPT_ID
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChargeActivity : AppCompatActivity() {
    protected lateinit var viewModel: MainViewModel
    protected lateinit var binding: FragmentChargeBinding

    protected lateinit var navHostFragment: NavHostFragment
    protected lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChargeBinding.inflate(layoutInflater)

        val token = runBlocking { UserPreferences(this@ChargeActivity).authToken.first() }
        val api = RemoteDataSource().buildApi(MainApi::class.java, token)
        val db = AppDatabase(this)

        val factory = ViewModelFactory(MainRepository(api,db))
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        intent.getStringExtra(EXTRA_RECEIPT_ID)?.let { viewModel.getReceipt(it) }

//        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_addition_to_pay) as NavHostFragment
//        navController = navHostFragment.navController


        setContentView(binding.root)
    }

    public fun navigateToPay(){
//        val action = ChargeFragmentDirections.navigateToPayReceiptFragment()
//        findNavController(R.id.fragment7).navigate(action)

    }
}