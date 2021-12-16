package com.amstrong.gaseapp.data.network

import com.amstrong.gaseapp.data.db.entities.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface MainApi {


    @GET("authentications/users/{id}")
    suspend fun getUser(
            @Path("id") employeeId: String
    ): Employee

    @GET("authentications/users")
    suspend fun getUsers(): List<Employee>


    @PUT("authentications/save-user/{id}")
    suspend fun saveUser(@Path("id") employeeId: String, @Body employee: Employee)


    @GET("items")
    suspend fun getAllItems(): List<Item>

    @GET("items/of-category/{matchingCategoryId}")
    suspend fun getItemsOfCategory(
            @Path("matchingCategoryId") categoryId: String
    ): List<Item>

    @GET("variants")
    suspend fun getVariants(): List<Variant>

    @GET("orders")
    suspend fun getOrders(): List<Order>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") orderId: String): Order

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Order

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") orderId: String): Order

    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") orderId: String, @Body order: Order): Order

    @PUT("orders/line/{id}")
    suspend fun updateOrderLine(@Path("id") orderId: String, @Body orderLine: OrderLine): OrderLine

    //receipts

    @GET("receipts")
    suspend fun getReceipts(): List<Receipt>

    @GET("receipts/at-the-date-of/{date}")
    suspend fun getReceiptsAtTheDateOf(
            @Path("date") date: String?
    ): List<Receipt>

    @GET("receipts/of-mine")
    suspend fun getReceiptsOfWaiter(): List<Receipt>

    @GET("receipts/of-mine/{date}")
    suspend fun getReceiptsOfWaiter(
            @Path("date", encoded = true) date: String?
    ): List<Receipt>

    @GET("receipts/{id}")
    suspend fun getReceipt(
            @Path("id") receiptId: String?,
    ): Receipt

    @POST("receipts")
    suspend fun createReceipt(@Body receipt: Receipt): Receipt

    @PUT("receipts/{id}")
    suspend fun updateReceipt(
            @Path("id") receiptId: String?,
            @Body receipt: Receipt
    ): Receipt

    @POST("receipts/divide/{id}")
    suspend fun divideReceipt(
            @Path("id") id: String?,
            @Body receiptsSourceDestination: ReceiptsSourceDestination
    ): ReceiptsSourceDestination

    @POST("receipts/merge/{id}")
    suspend fun mergeReceipts(
            @Path("id") id: String?,
            @Body selectedReceiptIds: List<String>
    ): Receipt

    @POST("receipts/forward/{waiterId}")
    suspend fun forwardReceipts(
            @Path("waiterId") waiterId: String,
            @Body selectedReceiptIds: List<String>
    ): List<Receipt>

    @POST("receipts/cancel/{id}")
    suspend fun cancelReceipts(
            @Body selectedReceiptIds: List<String>
    ): List<Receipt>

    @PUT("receipts/close/{id}")
    suspend fun closeReceipt(
            @Path("id") receiptId: String?,
    ): Receipt


    @PUT("receipts/process-lineItem/{lineId}")
    suspend fun processLineItem(
            @Path("lineId") lineId: String,
            @Body lineItem: LineItem
    ): LineItem


    @POST("receipts/pay/{id}")
    suspend fun payReceipt(
            @Path("id") receiptId: String?,
            @Body payments: List<Payment>,
    ): Receipt

    @POST("receipts/declare/{id}")
    suspend fun declareReceipt(
            @Path("id") receiptId: String?,
    ): Receipt

    /*
    @POST("receipts/report/{id}")
    suspend fun printReceipt(
            @Path("id") receiptId: String?,
    ): ResponseBody

    */

    //
    @GET("categories")
    suspend fun getAllCategories(): List<Category>

    @GET("categories/available-for-sale")
    suspend fun getCategoriesAvailable4Sale(): List<Category>

    @GET("categories/{id}")
    suspend fun getCategory(
            @Path("id") category_id: String,
    ): Category

    @PUT("categories/{id}")
    suspend fun saveCategory(
            @Path("id") categoryId: String,
            @Body category: Category
    ): Category


    @GET("stores")
    suspend fun getStores(): List<Store>

    @GET("suppliers")
    suspend fun getSuppliers(): List<Supplier>

    @GET("workstations/{id}")
    suspend fun getWorkstation(
            @Path("id") workstation_id: String,
    ): Workstation

    @GET("workstations")
    suspend fun getWorkstations(): List<Workstation>

    @POST("workstations")
    suspend fun createWorkstation(@Body workstation: Workstation): Workstation

    @PUT("workstations/{id}")
    suspend fun saveWorkstation(@Path("id") workstationId: String, @Body workstation: Workstation): Workstation


}