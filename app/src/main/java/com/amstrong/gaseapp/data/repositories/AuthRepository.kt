package com.amstrong.gaseapp.data.repositories

import com.amstrong.gaseapp.data.UserPreferences
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.response.UserCreateDto
import com.amstrong.gaseapp.data.response.UserLoginDto
import com.amstrong.mvvmsampleproject.data.db.AppDatabase

class AuthRepository(
        private val api: AuthApi,
        private val preferences: UserPreferences,
        private val db: AppDatabase
) : BaseRepository() {
    var employee: Employee? = null

    suspend fun login(
            userLoginDto: UserLoginDto
    ) = safeApiCall {
//        if (userLoginDto.open_id.count()==4) api.login(userLoginDto) else api.loginWithEmail(userLoginDto)
        api.login(userLoginDto)
    }


    suspend fun selectEmployee(employee: Employee?) = safeApiCall {
        this.employee = employee
        return@safeApiCall this.employee
    }


    suspend fun getEmployees() = safeApiCall {
        api.getEmployees()
    }

    suspend fun getEmployeesCount() = safeApiCall {
        api.getEmployeesCount()
    }


    suspend fun registerWorkstation(
            workstation: Workstation
    ) = safeApiCall {
        api.createWorkstation(workstation)
    }

    suspend fun getWorkstations() = safeApiCall {
        api.getWorkstations()
    }

    suspend fun registerUser(
            employeeId: String?,
            userCreateDto: UserCreateDto
    ) = safeApiCall {
        return@safeApiCall if (employeeId==null) api.saveUser(userCreateDto) else api.saveUser(employeeId, userCreateDto)
    }

    suspend fun saveAuthToken(token: String) {
        preferences.saveAuthToken(token)
    }

    suspend fun clearEverything() {
        preferences.clearEverything()
    }

    suspend fun saveEmployeeData(employee: Employee) {
        preferences.saveEmployeeData(employee)
    }


    suspend fun truncateLineItems() = safeApiCall {
        db.getLineItemDao().truncateLineItemsFromRoom()
    }


}