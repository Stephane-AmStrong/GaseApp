package com.amstrong.gaseapp.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.network.RemoteDataSource
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.databinding.ActivityAuthBinding
import com.amstrong.gaseapp.ui.MainActivity
import com.amstrong.gaseapp.ui.base.ViewModelFactory
import com.amstrong.gaseapp.utils.startNewActivity
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    protected lateinit var viewModel: AuthViewModel
    protected lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val token = runBlocking { UserPreferences(this@AuthActivity).authToken.first() }
        val api = RemoteDataSource().buildApi(AuthApi::class.java, token)
        val db = AppDatabase(this)

        val factory = ViewModelFactory(AuthRepository(api, UserPreferences(this@AuthActivity), db))

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        navController = findNavController(R.id.nav_host_fragment)

        runBlocking { viewModel.clearPreferences() }

        viewModel.deleteAllLineItemsOfSqlDB()
        viewModel.getWorkstations()
        viewModel.getEmployeesCount()

        viewModel.loginResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveAuthToken(it.value.token!!)
                        viewModel.saveEmployeeData(it.value.employee)


                        /*
                        if (it.value.employee.workstation.canTakeOrders){
                            val navGraph = navController.graph;
                            navGraph.startDestination = R.id.nav_waiter;
                            navController.graph = navGraph
                        }else{
                            val navGraph = navController.graph;
                            navGraph.startDestination = R.id.nav_my_workstation;
                            navController.graph = navGraph
                        }
                        */

                        startNewActivity(MainActivity::class.java)
                    }
                }
            }
        })

        viewModel.workstations.observe(this, Observer {
            when(it){

                is Resource.Success ->{
                    updateUIWorkstations(it.value)
                }

                is Resource.Failure ->{

                }

            }
        })

//        val navGraph = navController.graph;
//        navGraph.startDestination = R.id.nav_login_fragment;
//        navController.graph = navGraph


        viewModel.employeesCount.observe(this, Observer {
            when(it){

                is Resource.Success ->{
                    updateUIEmployeesCount(it.value)
                }

                is Resource.Failure ->{

                }

            }
        })

        

    }

    private fun updateUIWorkstations(workstations: List<Workstation>){
        if (!workstations.any()) viewModel.registerWorkstation(
                 Workstation(null, resources.getString(R.string.administrator_workstation), true, true, true, true, true, true, true, true,true, Date())
        )
    }


    private fun updateUIEmployeesCount(employeesCount: Int){
        if (employeesCount<=0) {
            val navGraph = navController.graph;
            navGraph.startDestination = R.id.registrationFragment;
            navController.graph = navGraph
        }else{
            val navGraph = navController.graph;
            navGraph.startDestination = R.id.loginFragment;
            navController.graph = navGraph
        }
    }

    private fun updateUIEmployee(employee: Employee){
//        Log.d("TAG", "updateUIEmployees: employee.name ${employee.name}")
//        viewModel.getEmployeesCount()
    }

}