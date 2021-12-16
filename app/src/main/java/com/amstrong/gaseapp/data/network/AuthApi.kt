package com.amstrong.gaseapp.data.network

import com.amstrong.gaseapp.data.db.entities.Employee
import com.amstrong.gaseapp.data.db.entities.Workstation
import com.amstrong.gaseapp.data.response.LoginResponse
import com.amstrong.gaseapp.data.response.UserLoginDto
import com.amstrong.gaseapp.data.response.UserCreateDto
import okhttp3.ResponseBody
import retrofit2.http.*

interface AuthApi {


    @POST("authentications/register-user")
    suspend fun saveUser(@Body userRegisterDto: UserCreateDto): Employee

    @PUT("authentications/save-user/{id}")
    suspend fun saveUser(@Path("id") employeeId:String, @Body userRegisterDto: UserCreateDto): Employee

    @POST("authentications/users/disable/{id}")
    suspend fun disableUser(@Path("id") employeeId:String)


    @POST("authentications/users/enable/{id}")
    suspend fun enableUser(@Path("id") employeeId:String)


    @POST("authentications/login")
    suspend fun login(
        @Body userLoginDto: UserLoginDto,
        ) : LoginResponse

    @POST("authentications/login-with-email")
    suspend fun loginWithEmail(
        @Body userLoginDto: UserLoginDto,
        ) : LoginResponse

    @GET("authentications/users")
    suspend fun getEmployees() : List<Employee>

    @GET("authentications/users-count")
    suspend fun getEmployeesCount() : Int

    @POST("logout")
    suspend fun logout(): ResponseBody

    @GET("workstations/{id}")
    suspend fun getWorkstation(
            @Path("id") workstation_id:String,
    ): Workstation

    @GET("workstations")
    suspend fun getWorkstations(
    ): List<Workstation>

    @POST("workstations")
    suspend fun createWorkstation(@Body workstation: Workstation): Workstation

}