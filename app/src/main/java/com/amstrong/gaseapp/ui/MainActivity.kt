package com.amstrong.gaseapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialcab.attached.AttachedCab
import com.amstrong.gaseapp.R
import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.db.entities.Receipt
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.gaseapp.data.network.RemoteDataSource
import com.amstrong.gaseapp.data.network.RemoteDataSource.Companion.BASE_URL
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.databinding.ActivityMainBinding
import com.amstrong.gaseapp.databinding.DrawerHeaderBinding
import com.amstrong.gaseapp.ui.auth.AuthActivity
import com.amstrong.gaseapp.ui.base.ViewModelFactory
import com.amstrong.gaseapp.ui.dialogs.*
import com.amstrong.gaseapp.ui.waiter.*
import com.amstrong.gaseapp.util.toast
import com.amstrong.gaseapp.utils.startNewActivity
import com.amstrong.gaseapp.utils.visible
import com.amstrong.mvvmsampleproject.data.db.AppDatabase
import com.google.android.material.navigation.NavigationView
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*


class MainActivity : AppCompatActivity() {
    private var cab: AttachedCab? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    protected lateinit var binding: ActivityMainBinding
    protected lateinit var toolbar: Toolbar

    protected lateinit var employeeId: String
    protected lateinit var employeeNom: String
    protected lateinit var employeeWorkstationName: String

    protected var canTakeOrders: Boolean = false
    protected var canManageCategories: Boolean = false
    protected var canPrepareMealsOrDrinks: Boolean = false
    protected var canCashIn: Boolean = false

    protected var canManagePurchaseOrders: Boolean = false
    protected var canManageHall: Boolean = false
    protected var canManagekitchen: Boolean = false
    protected var canManageUsers: Boolean = false
    protected var canManageWorkstations: Boolean = false

    private lateinit var onDestinationChangedListener: NavController.OnDestinationChangedListener
    private var token : String? = null

    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var myReceiptId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        toolbar = binding.appBarMain.toolbar

        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        val headerView = binding.navView.getHeaderView(0)
        val headerBinding: DrawerHeaderBinding = DrawerHeaderBinding.bind(headerView)

        token = runBlocking { UserPreferences(this@MainActivity).authToken.first() }

        val api = RemoteDataSource().buildApi(MainApi::class.java, token)
        val db = AppDatabase(this)

        val factory = ViewModelFactory(MainRepository(api, db))

        mainViewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        //
        employeeId = runBlocking { UserPreferences(this@MainActivity).employeeId.first()!! }
        employeeNom = runBlocking { UserPreferences(this@MainActivity).employeeNom.first()!! }
        employeeWorkstationName = runBlocking { UserPreferences(this@MainActivity).employeeWorkstation.first()!! }

        canManagePurchaseOrders = runBlocking { UserPreferences(this@MainActivity).canManagePurchaseOrders.first()!! }
        canManageHall = runBlocking { UserPreferences(this@MainActivity).canManageHall.first()!! }
        canManagekitchen = runBlocking { UserPreferences(this@MainActivity).canManagekitchen.first()!! }
        canManageUsers = runBlocking { UserPreferences(this@MainActivity).canManageUsers.first()!! }
        canManageCategories = runBlocking { UserPreferences(this@MainActivity).canManageCategories.first()!! }
        canManageWorkstations = runBlocking { UserPreferences(this@MainActivity).canManageWorkstations.first()!! }

        canTakeOrders = runBlocking { UserPreferences(this@MainActivity).canTakeOrders.first()!! }
        canPrepareMealsOrDrinks = runBlocking { UserPreferences(this@MainActivity).canPrepareMealsOrDrinks.first()!! }
        canCashIn = runBlocking { UserPreferences(this@MainActivity).canCashIn.first()!! }

        if (token==null){
            startNewActivity(AuthActivity::class.java)
        }else{

            /*
                val canTakeOrders: Boolean,
                val canPrepareMealsOrDrinks: Boolean,
                val canCashIn: Boolean,

                val canManagePurchaseOrder: Boolean,
                val canManageHall: Boolean,
                val canManagekitchen: Boolean,
                val canManageUsers: Boolean,
                val canManageWorkstations: Boolean,
            */

            //
            mainViewModel.getLoggedInUser(employeeId)

            headerBinding.txtUserName.setText(employeeNom)
            headerBinding.txtWorkstationName.setText(employeeWorkstationName)
            //
            binding.navView.menu.findItem(R.id.nav_waiter).setVisible(canTakeOrders || canManageHall)
            binding.navView.menu.findItem(R.id.nav_my_orders).setVisible(canTakeOrders  || canManageHall)
            //
            binding.navView.menu.findItem(R.id.nav_my_workstation).setVisible(canPrepareMealsOrDrinks)
            binding.navView.menu.findItem(R.id.nav_my_workstation).setTitle(employeeWorkstationName)

            //
            binding.navView.menu.findItem(R.id.nav_users).setVisible(canManageUsers)
            //
            binding.navView.menu.findItem(R.id.nav_workstations).setVisible(canManageWorkstations)
            binding.navView.menu.findItem(R.id.nav_categories).setVisible(canManageCategories)
            //
            binding.navView.menu.findItem(R.id.nav_purchase_orders).setVisible(canManagePurchaseOrders)
            //
            if (canTakeOrders){
//                val navGraph = navController.graph;
//                navGraph.startDestination = R.id.nav_waiter;
//                navController.graph = navGraph

            }else if (!(canTakeOrders || canManageHall) && navController.graph.startDestination != R.id.nav_my_workstation){
                navGraph.startDestination = R.id.nav_my_workstation
                navController.graph = navGraph
            }


            appBarConfiguration = AppBarConfiguration(
                    setOf(R.id.nav_my_workstation, R.id.nav_workstations, R.id.nav_waiter, R.id.nav_users, R.id.nav_my_orders, R.id.nav_my_profile, R.id.nav_categories, R.id.nav_purchase_orders),
                    drawerLayout)

            setupActionBarWithNavController(navController, appBarConfiguration)
            binding.navView.setupWithNavController(navController)

            // waiter notification
            mainViewModel.getReceiptsOfWaiter(null)

            mainViewModel.myReceipts.observe(this, {
                when (it) {
                    is Resource.Success -> {
                        updateUIWithMyReceipts(it.value)
                    }
                }
            })



        }


        val hubURL = BASE_URL.replace("api","hub")+ "chat";

        val hubConnection = HubConnectionBuilder.create(hubURL).build()

        hubConnection.start().subscribe(object : CompletableObserver {

            override fun onSubscribe(d: Disposable) {
                Log.d("SocketSrv", "hub disposed ${d.isDisposed}")
            }

            override fun onError(e: Throwable) {
                Log.d("SocketSrv", "hub error ${e}")
                Log.d("SocketSrv", "hub url ${hubURL}")
            }

            override fun onComplete() {
                Log.d("SocketSrv", "hub Connected")
            }
        })

        hubConnection.on("NewOrder", { target, message ->
            scope.launch(Dispatchers.Main) {
                Log.d("SocketSrv", "NewOrder : message ${message}")

                mainViewModel.getReceipts()
            }
        }, String::class.java, String::class.java)



        hubConnection.on("FoodReady", { target, message ->
            scope.launch(Dispatchers.Main) {
                Log.d("SocketSrv", "FoodReady : message ${message}")

                //mainViewModel.getReceiptsOfWaiter(null)
                mainViewModel.refreshSelectedReceipt()
                mainViewModel.getReceiptsOfWaiter(null)
            }
        }, String::class.java, String::class.java)



        hubConnection.on("Inventory", { message ->
            scope.launch(Dispatchers.Main) {
                Log.d("SocketSrv", "Inventory : message ${message}")

                //rafraichir la liste des receipt
                mainViewModel.getAllItems()


            }
        }, String::class.java)

    }


    private fun updateUIWithMyReceipts(receipts: List<Receipt>) {
        if(receipts.any{it.line_items.any{it.ready_at!=null && it.served_at == null}}) this.notify("commandes traité", "", R.drawable.ic_baseline_outdoor_grill_24, pendingIntent(this))
    }


    override fun onStop() {
        super.onStop()
        startNewActivity(AuthActivity::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun notify(title: String, message: String, smallIcon: Int) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)



        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    fun notify(title: String, message: String, smallIcon: Int, pendingIntent: PendingIntent) {

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true)



        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
//                vibrationPattern =
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "com.amstrong.gaseapp.channel"
    }

    fun pendingIntent(context: Context): PendingIntent {
        return NavDeepLinkBuilder(context)
                .setGraph(R.navigation.main_navigation)
                .setDestination(R.id.nav_my_orders)
                .createPendingIntent()
    }

}