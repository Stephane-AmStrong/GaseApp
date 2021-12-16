package com.amstrong.gaseapp.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.amstrong.gaseapp.data.db.entities.Employee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
        name = "my_data_store"
    )

    suspend fun saveAuthToken(authToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH] = authToken
        }
    }

    suspend fun saveEmployeeData(employee: Employee) {
        dataStore.edit { preferences ->
            preferences[KEY_EMPLOYEE_ID] = employee.id
            preferences[KEY_EMPLOYEE_NAME] = employee.name

            preferences[KEY_WORKSTATION_ID] = employee.workstation.id!!
            preferences[KEY_EMPLOYEE_WORKSTATION] = employee.workstation.name

            preferences[KEY_CAN_MANAGE_PURCHASE_ORDER] = employee.workstation.canManagePurchaseOrders
            preferences[KEY_CAN_MANAGE_HALL] = employee.workstation.canManageHall
            //preferences[KEY_CAN_MANAGE_KITCHEN] = employee.workstation.canTakeOrders || employee.workstation.canCashIn
            preferences[KEY_CAN_MANAGE_KITCHEN] = employee.workstation.canManagekitchen
            preferences[KEY_CAN_MANAGE_USERS] = employee.workstation.canManageUsers
            preferences[KEY_CAN_MANAGE_CATEGORIES] = employee.workstation.canManageUsers
            preferences[KEY_CAN_MANAGE_WORKSTATIONS] = employee.workstation.canManageWorkstations
            preferences[KEY_CAN_TAKE_ORDERS] = employee.workstation.canTakeOrders
            preferences[KEY_CAN_PREPARE_MEALS_OR_DRINKS] = employee.workstation.canPrepareMealsOrDrinks
            preferences[KEY_CAN_CASHIN] = employee.workstation.canCashIn
        }
    }


    val authToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }


    val workstation_id: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_WORKSTATION_ID]
        }

    val employeeId: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_EMPLOYEE_ID]
        }

    val employeeNom: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_EMPLOYEE_NAME]
        }

    val employeeWorkstation: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_EMPLOYEE_WORKSTATION]
        }


    val canManagePurchaseOrders: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_PURCHASE_ORDER]
        }

    val canManageHall: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_HALL]
        }

    val canManagekitchen: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_KITCHEN]
        }

    val canManageUsers: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_USERS]
        }

    val canManageCategories: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_CATEGORIES]
        }

    val canManageWorkstations: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_MANAGE_WORKSTATIONS]
        }

    val canTakeOrders: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_TAKE_ORDERS]
        }

    val canPrepareMealsOrDrinks: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_PREPARE_MEALS_OR_DRINKS]
        }


    val canCashIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_CAN_CASHIN]
        }




    suspend fun clearEverything() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        private val KEY_AUTH = preferencesKey<String>("key_auth")
        private val KEY_WORKSTATION_ID = preferencesKey<String>("key_workstation_id")
        private val KEY_EMPLOYEE_ID = preferencesKey<String>("key_employee_id")
        private val KEY_EMPLOYEE_NAME = preferencesKey<String>("key_employee_nom")

        private val KEY_EMPLOYEE_WORKSTATION = preferencesKey<String>("key_employee_workstation")

        private val KEY_CAN_MANAGE_PURCHASE_ORDER = preferencesKey<Boolean>("key_can_manage_purchase_order")
        private val KEY_CAN_MANAGE_HALL = preferencesKey<Boolean>("key_can_manage_hall")
        private val KEY_CAN_MANAGE_KITCHEN = preferencesKey<Boolean>("key_can_manage_kitchen")
        private val KEY_CAN_MANAGE_USERS = preferencesKey<Boolean>("key_can_manage_users")
        private val KEY_CAN_MANAGE_CATEGORIES = preferencesKey<Boolean>("key_can_manage_categories")
        private val KEY_CAN_MANAGE_WORKSTATIONS = preferencesKey<Boolean>("key_can_manage_workstations")
        private val KEY_CAN_TAKE_ORDERS = preferencesKey<Boolean>("key_can_take_orders")
        private val KEY_CAN_PREPARE_MEALS_OR_DRINKS = preferencesKey<Boolean>("KEY_CAN_PREPARE_MEALS_OR_DRINKS")
        private val KEY_CAN_CASHIN = preferencesKey<Boolean>("key_can_cashin")
    }
}
