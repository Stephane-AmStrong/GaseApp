package com.amstrong.gaseapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.ui.base.BaseViewModel
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.AuthRepository
import com.amstrong.gaseapp.data.response.LoginResponse
import com.amstrong.gaseapp.data.response.UserCreateDto
import com.amstrong.gaseapp.data.response.UserLoginDto
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
) : BaseViewModel(repository) {

    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    private val _workstation: MutableLiveData<Resource<Workstation>> = MutableLiveData()
    private val _workstations: MutableLiveData<Resource<List<Workstation>>> = MutableLiveData()
    private val _employee: MutableLiveData<Resource<Employee>> = MutableLiveData()
    private val _employees: MutableLiveData<Resource<List<Employee>>> = MutableLiveData()
    private val _employeesCount: MutableLiveData<Resource<Int>> = MutableLiveData()
    private val _lineItems: MutableLiveData<Resource<List<LineItem>>> = MutableLiveData()
    private val _selectedEmployee: MutableLiveData<Resource<Employee?>> = MutableLiveData()



    val selectedEmployee: LiveData<Resource<Employee?>>
        get() = _selectedEmployee

    val employee: LiveData<Resource<Employee>>
        get() = _employee

    val employeesCount: LiveData<Resource<Int>>
        get() = _employeesCount

    val employees: LiveData<Resource<List<Employee>>>
        get() = _employees

    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    val lineItems: LiveData<Resource<List<LineItem>>>
        get() = _lineItems


    val workstation: LiveData<Resource<Workstation>>
        get() = _workstation

    val workstations: LiveData<Resource<List<Workstation>>>
        get() = _workstations


    fun login(
        userLoginDto: UserLoginDto
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(userLoginDto)
    }


    fun selectEmployee(employee: Employee?) = viewModelScope.launch {
        _selectedEmployee.value = Resource.Loading
        _selectedEmployee.value = repository.selectEmployee(employee)
    }

    fun getEmployeesCount() = viewModelScope.launch {
        _employeesCount.value = Resource.Loading
        _employeesCount.value = repository.getEmployeesCount()
    }

    fun getEmployees() = viewModelScope.launch {
        _employees.value = Resource.Loading
        _employees.value = repository.getEmployees()
    }

    fun getWorkstations() = viewModelScope.launch {
        _workstations.value = Resource.Loading
        _workstations.value = repository.getWorkstations()
    }

    fun registerWorkstation(
            workstation: Workstation
    ) = viewModelScope.launch {
        _workstation.value = Resource.Loading
        _workstation.value = repository.registerWorkstation(workstation)
        _workstations.value = repository.getWorkstations()
    }

    fun registerUser(
        employeeId: String?,
        userCreateDto: UserCreateDto
    ) = viewModelScope.launch {
        _employee.value = Resource.Loading
        _employee.value = repository.registerUser(employeeId, userCreateDto)

        _employees.value = Resource.Loading
        _employees.value = repository.getEmployees()

        _employeesCount.value = repository.getEmployeesCount()
    }

    suspend fun saveAuthToken(token: String) {
        repository.saveAuthToken(token)
    }

    suspend fun clearPreferences() {
        repository.clearEverything()
    }

    suspend fun saveEmployeeData(employee: Employee) {
        repository.saveEmployeeData(employee)
    }

    fun deleteAllLineItemsOfSqlDB() = viewModelScope.launch {
        _lineItems.value = Resource.Loading
        repository.truncateLineItems()
    }
}















































