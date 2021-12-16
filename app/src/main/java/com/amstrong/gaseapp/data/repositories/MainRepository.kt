package com.amstrong.gaseapp.data.repositories

import android.content.ContentValues
import android.util.Log
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.data.network.MainApi
import com.amstrong.mvvmsampleproject.data.db.AppDatabase

class MainRepository(
        private val api: MainApi,
        private val db: AppDatabase
) : BaseRepository() {

    private var _selectedOrderLine: OrderLine? = null
    private lateinit var lineItemWithTaxes: LineItemWithTaxes
    private lateinit var receipt: Receipt
    private lateinit var receiptSource: Receipt
    private lateinit var receiptDestination: Receipt
    private var _selectedWorkstation: Workstation? = null
    private var _selectedReceiptIds = listOf<String>()
    private var _selectedCategory: Category? = null
    private var _selectedOrder: Order? = null
    private var _selectedReceipt: Receipt? = null
    private lateinit var _diningOption: String


    suspend fun getUser(id:String) = safeApiCall {
        api.getUser(id)
    }

    suspend fun registerUser(
            id: String,
            userReadDto: Employee
    ) = safeApiCall {
        api.saveUser(id, userReadDto)
    }

    suspend fun getUsers() = safeApiCall {
        api.getUsers()
    }


    suspend fun selectedWorkstation(selectedWorkstation: Workstation?) = safeApiCall {
        _selectedWorkstation = selectedWorkstation
        return@safeApiCall _selectedWorkstation
    }

    suspend fun selectedCategory(category: Category?) = safeApiCall {
        _selectedCategory = category
        return@safeApiCall _selectedCategory
    }

    suspend fun saveCategory(
            categoryId: String,
            category: Category
    ) = safeApiCall {
        api.saveCategory(categoryId, category)
    }


    suspend fun getWorkstations() = safeApiCall {
        api.getWorkstations()
    }

    suspend fun registerWorkstation(
            workstationId: String?,
            workstation: Workstation
    ) = safeApiCall {
        return@safeApiCall if (workstationId==null) api.createWorkstation(workstation) else api.saveWorkstation(workstationId, workstation)
    }

    suspend fun getWorkstation(workstation_id : String) = safeApiCall {
        api.getWorkstation(workstation_id)
    }

    suspend fun getCategoriesAvailable4Sale() = safeApiCall {
        api.getCategoriesAvailable4Sale()
    }

    suspend fun getAllCategories() = safeApiCall {
        api.getAllCategories()
    }

    suspend fun getCategory(category_id:String) = safeApiCall {
        api.getCategory(category_id)
    }

    suspend fun getItemsOfCategory(categoryId:String) = safeApiCall {
        api.getItemsOfCategory(categoryId)
    }

    suspend fun getAllItems() = safeApiCall {
        api.getAllItems()
    }


    suspend fun getVariants() = safeApiCall {
        api.getVariants()
    }


    suspend fun getStores() = safeApiCall {
        api.getStores()
    }

    suspend fun getSuppliers() = safeApiCall {
        api.getSuppliers()
    }

    suspend fun registerOrder(order: Order) = safeApiCall {
        return@safeApiCall if (order.id==null) api.createOrder(order) else api.updateOrder(order.id!!, order)
    }

    suspend fun deleteOrder(id: String) = safeApiCall {
        api.deleteOrder(id)
    }

    suspend fun updateOrderLine(orderLine: OrderLine) = safeApiCall {
        api.updateOrderLine(orderLine.id!!, orderLine)
    }

    suspend fun getOrders() = safeApiCall {
        api.getOrders()
    }

    suspend fun getOrder(orderId: String) = safeApiCall {
        api.getOrder(orderId)
    }

    suspend fun setOrder(order:Order?) = safeApiCall {
        this._selectedOrder = order
        return@safeApiCall this._selectedOrder
    }


    suspend fun setOrderLine(orderLine:OrderLine?) = safeApiCall {
        _selectedOrderLine = orderLine
        return@safeApiCall orderLine
    }


    suspend fun getReceipts() = safeApiCall {
        api.getReceipts()
    }

    suspend fun getReceiptsAtTheDateOf(date : String?) = safeApiCall {
        return@safeApiCall if (date!=null) api.getReceiptsAtTheDateOf(date) else api.getReceipts()
    }

    suspend fun getReceiptsOfWaiter(date : String?) = safeApiCall {
        return@safeApiCall if (date!=null) api.getReceiptsOfWaiter(date) else api.getReceiptsOfWaiter()
    }

    suspend fun selectReceipt(receipt:Receipt) = safeApiCall {
        this.receipt = receipt
        return@safeApiCall receipt
    }

    suspend fun selectReceiptSource(receipt:Receipt) = safeApiCall {
        this.receiptSource = receipt
        return@safeApiCall receipt
    }

    suspend fun selectReceiptDestination(receipt:Receipt) = safeApiCall {
        this.receiptDestination = receipt
        return@safeApiCall receipt
    }

    suspend fun addLineItem2Source(lineItems: List<LineItem>) = safeApiCall {
        receiptSource.line_items.addAll(lineItems)
        return@safeApiCall receiptSource
    }

    suspend fun addLineItem2Destination(lineItems: List<LineItem>) = safeApiCall {
        receiptDestination.line_items.addAll(lineItems)
        return@safeApiCall receiptDestination
    }

    suspend fun removeLineItemFromSource(lineItems: List<LineItem>) = safeApiCall {
        receiptSource.line_items.removeAll(lineItems)
        return@safeApiCall receiptSource
    }

    suspend fun removeLineItemFromDestination(lineItems: List<LineItem>) = safeApiCall {
        receiptDestination.line_items.removeAll(lineItems)
        return@safeApiCall receiptDestination
    }

    suspend fun saveReceipt(receipt: Receipt) = safeApiCall {
        if (receipt.id==null) api.createReceipt(receipt) else api.updateReceipt(receipt.id, receipt)
    }

    suspend fun divideReceipt(id: String?, receiptsSourceDestination: ReceiptsSourceDestination) = safeApiCall {
        api.divideReceipt(id, receiptsSourceDestination)
    }

    suspend fun mergeReceipts(receiptId: String?, receiptIds: List<String>) = safeApiCall {
        api.mergeReceipts(receiptId, receiptIds)
    }

    suspend fun forwardReceipts(id: String, receiptIds: List<String>) = safeApiCall {
        api.forwardReceipts(id, receiptIds)
    }

    suspend fun cancelReceipts(receiptIds: List<String>) = safeApiCall {
        api.cancelReceipts(receiptIds)
    }

    suspend fun closeReceipt(receiptId: String?) = safeApiCall {
        api.closeReceipt(receiptId)
    }


    suspend fun selectReceiptIds(selectedReceiptIds: List<String>) = safeApiCall {
        _selectedReceiptIds = selectedReceiptIds
        return@safeApiCall _selectedReceiptIds
    }


    suspend fun payReceipt(receiptId: String?,payments: List<Payment>) = safeApiCall {
        api.payReceipt(receiptId, payments)
    }

    suspend fun declareReceipt(receiptId: String?) = safeApiCall {
        api.declareReceipt(receiptId)
    }

    /*
    suspend fun printReceipt(receiptId: String?) = safeApiCall {
        api.printReceipt(receiptId)
    }
    */

    suspend fun selectLineItemWithTaxes(lineItemWithTaxes: LineItemWithTaxes) = safeApiCall {
        this.lineItemWithTaxes = lineItemWithTaxes
        return@safeApiCall lineItemWithTaxes
    }

    suspend fun selectDiningOption(diningOption: String) = safeApiCall {
        _diningOption = diningOption
        return@safeApiCall _diningOption
    }

    suspend fun getReceipt(receiptId:String?) = safeApiCall {
        api.getReceipt(receiptId)
    }

    suspend fun setReceipt(receipt: Receipt?) = safeApiCall {
        _selectedReceipt = receipt
        return@safeApiCall _selectedReceipt
    }

    suspend fun saveLineItemsToRoom(lineItem: LineItem) = safeApiCall {
        //Log.d(ContentValues.TAG, "lineItemsWithTaxes: lineTaxes.count from MainRepository " + lineItem.line_taxes.count())

        val LineItem_number_fk = db.getLineItemDao().saveLineItemsToRoom(lineItem)
        lineItem.line_taxes.map { it.LineItem_number_fk = LineItem_number_fk }
        db.getLineItemDao().saveLineTaxesToRoom(lineItem.line_taxes)

    }

    suspend fun saveLineItemsToRoom(lineItems : List<LineItem>) = safeApiCall {
        db.getLineItemDao().saveLineItemsToRoom(lineItems)
    }

    suspend fun getLineItemWithTaxesFromRoom() = safeApiCall {
        db.getLineItemDao().getLineItemWithTaxesFromRoom()
    }

    suspend fun truncateLineItemsFromRoom() = safeApiCall {
        db.getLineItemDao().truncateLineItemsFromRoom()
        db.getLineItemDao().truncateLineTaxesFromRoom()
    }

    suspend fun removeLineItemsFromRoom(lineItem: LineItem) = safeApiCall {
        db.getLineItemDao().removeLineItemsFromRoom(lineItem)
    }


    suspend fun processLineItem(lineId : String,lineItem : LineItem) = safeApiCall {
        api.processLineItem(lineId, lineItem)
    }



//    suspend fun getInventoryLevelsFromSqLite() = safeApiCall {
//        db.getInventoryLevelDao().getInventoryLevelsFromSqLite()
//    }
//
//    suspend fun deleteAllInventoryLevelsOfSqlDB() = safeApiCall {
//        db.getInventoryLevelDao().deleteAllInventoryLevelsOfSqlDB()
//    }
//
//
//    //InventoryLevelDao
//    suspend fun saveInventoryLevelToSqlDB(inventoryLevel: InventoryLevel) = safeApiCall {
//        db.getInventoryLevelDao().saveInventoryLevelToSqlDB(inventoryLevel)
//    }
//
//    suspend fun getInventoryLevelFromSqLite(variantId: String) = safeApiCall {
//        db.getInventoryLevelDao().getInventoryLevelFromSqLite(variantId)
//    }
//
//    suspend fun saveInventoryLevelsToSqlDB(inventoryLevels : List<InventoryLevel>) = safeApiCall {
//        db.getInventoryLevelDao().saveInventoryLevelsToSqlDB(inventoryLevels)
//    }
//


}