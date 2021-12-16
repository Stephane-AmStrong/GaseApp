package com.amstrong.gaseapp.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amstrong.gaseapp.data.db.entities.*
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes
import com.amstrong.gaseapp.ui.base.BaseViewModel
import com.amstrong.gaseapp.data.network.Resource
import com.amstrong.gaseapp.data.repositories.MainRepository
import com.amstrong.gaseapp.utils.toLineItemWithTaxes
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class MainViewModel(
        private val repository: MainRepository,
) : BaseViewModel(repository) {

    private val _selectedCategory: MutableLiveData<Resource<Category?>> = MutableLiveData()

    private val _user: MutableLiveData<Resource<Employee>> = MutableLiveData()
    private val _users: MutableLiveData<Resource<List<Employee>>> = MutableLiveData()
    private val _selectedWorkstation: MutableLiveData<Resource<Workstation?>> = MutableLiveData()
    private val _workstation: MutableLiveData<Resource<Workstation>> = MutableLiveData()
    private val _workstations: MutableLiveData<Resource<List<Workstation>>> = MutableLiveData()

    //private val _receiptPrintMessage: MutableLiveData<Resource<ResponseBody>> = MutableLiveData()

    private val _allCategories: MutableLiveData<Resource<List<Category>>> = MutableLiveData()
    private val _categoriesAvailable4Sale: MutableLiveData<Resource<List<Category>>> = MutableLiveData()
    private val _category: MutableLiveData<Resource<Category>> = MutableLiveData()

    private val _allItems: MutableLiveData<Resource<List<Item>>> = MutableLiveData()
    private val _itemsFromCategory: MutableLiveData<Resource<List<Item>>> = MutableLiveData()

    private val _variants: MutableLiveData<Resource<List<Variant>>> = MutableLiveData()

    private val _order: MutableLiveData<Resource<Order>> = MutableLiveData()
    private val _selectedOrder: MutableLiveData<Resource<Order?>> = MutableLiveData()
    private val _registeredOrder: MutableLiveData<Resource<Order?>> = MutableLiveData()

    private val _selectedOrderLine: MutableLiveData<Resource<OrderLine?>> = MutableLiveData()
    private val _registeredOrderLine: MutableLiveData<Resource<OrderLine?>> = MutableLiveData()

    private val _orders: MutableLiveData<Resource<List<Order>>> = MutableLiveData()
    private val _stores: MutableLiveData<Resource<List<Store>>> = MutableLiveData()
    private val _suppliers: MutableLiveData<Resource<List<Supplier>>> = MutableLiveData()

    private val _receipts: MutableLiveData<Resource<List<Receipt>>> = MutableLiveData()
    private val _receipt: MutableLiveData<Resource<Receipt?>> = MutableLiveData()
    private val _mergedReceipt: MutableLiveData<Resource<Receipt?>> = MutableLiveData()
    private val _forwardedReceipts: MutableLiveData<Resource<List<Receipt>>> = MutableLiveData()
    private val _cancelledReceipts: MutableLiveData<Resource<List<Receipt>>> = MutableLiveData()
    private val _receiptSourceDestination: MutableLiveData<Resource<ReceiptsSourceDestination>> = MutableLiveData()
    private val _receiptSource: MutableLiveData<Resource<Receipt>> = MutableLiveData()
    private val _receiptDestination: MutableLiveData<Resource<Receipt>> = MutableLiveData()
    private val _paidReceipt: MutableLiveData<Resource<Receipt>> = MutableLiveData()
    private val _normalizedReceipt: MutableLiveData<Resource<Receipt>> = MutableLiveData()
    private val _myReceipts: MutableLiveData<Resource<List<Receipt>>> = MutableLiveData()
    private val _receiptSaved: MutableLiveData<Resource<Receipt?>> = MutableLiveData()

    private var _receiptId: String? = null
    private val _selectedReceipt: MutableLiveData<Resource<Receipt?>> = MutableLiveData()
    private val _diningOption: MutableLiveData<Resource<String>> = MutableLiveData()
    private val _selectedReceiptIds: MutableLiveData<Resource<List<String>>> = MutableLiveData()
    //    private val _lineItems: MutableLiveData<Resource<List<LineItem>>> = MutableLiveData()
    private val _lineItemWithTaxes: MutableLiveData<Resource<LineItemWithTaxes?>> = MutableLiveData()
    //private val _lineItems: MutableLiveData<Resource<List<LineItem>>> = MutableLiveData()
    private val _lineItemsWithTaxes: MutableLiveData<Resource<List<LineItemWithTaxes>>> = MutableLiveData()


    val user: LiveData<Resource<Employee>>
        get() = _user

    val users: LiveData<Resource<List<Employee>>>
        get() = _users

    val diningOption: LiveData<Resource<String>>
        get() = _diningOption

    val workstation: LiveData<Resource<Workstation>>
        get() = _workstation

    val selectedWorkstation: LiveData<Resource<Workstation?>>
        get() = _selectedWorkstation


    val workstations: LiveData<Resource<List<Workstation>>>
        get() = _workstations


    val allCategories: LiveData<Resource<List<Category>>>
        get() = _allCategories

    val categoriesAvailable4Sale: LiveData<Resource<List<Category>>>
        get() = _categoriesAvailable4Sale

    val selectedCategory: LiveData<Resource<Category?>>
        get() = _selectedCategory

    val order: LiveData<Resource<Order>>
        get() = _order

    val selectedOrder: LiveData<Resource<Order?>>
        get() = _selectedOrder

    val registeredOrder: LiveData<Resource<Order?>>
        get() = _registeredOrder

    val selectedOrderLine: LiveData<Resource<OrderLine?>>
        get() = _selectedOrderLine

    val registeredOrderLine: LiveData<Resource<OrderLine?>>
        get() = _registeredOrderLine

    val orders: LiveData<Resource<List<Order>>>
        get() = _orders

    val stores: LiveData<Resource<List<Store>>>
        get() = _stores

    val suppliers: LiveData<Resource<List<Supplier>>>
        get() = _suppliers


    val category: LiveData<Resource<Category>>
        get() = _category

    val allItems: LiveData<Resource<List<Item>>>
        get() = _allItems

    val itemsOfCategory: LiveData<Resource<List<Item>>>
        get() = _itemsFromCategory

    val variants: LiveData<Resource<List<Variant>>>
        get() = _variants

    val selectedReceiptIds: LiveData<Resource<List<String>>>
        get() = _selectedReceiptIds

    val selectedReceipt: LiveData<Resource<Receipt?>>
        get() = _selectedReceipt

    val receipts: LiveData<Resource<List<Receipt>>>
        get() = _receipts

    val mergedReceipt: LiveData<Resource<Receipt?>>
        get() = _mergedReceipt

    val forwardedReceipts: LiveData<Resource<List<Receipt>>>
        get() = _forwardedReceipts

    val cancelledReceipts: LiveData<Resource<List<Receipt>>>
        get() = _cancelledReceipts

    val receiptSource: LiveData<Resource<Receipt>>
        get() = _receiptSource

    val receiptDestination: LiveData<Resource<Receipt>>
        get() = _receiptDestination

    val receiptSourceDestination: LiveData<Resource<ReceiptsSourceDestination>>
        get() = _receiptSourceDestination

    val myReceipts: LiveData<Resource<List<Receipt>>>
        get() = _myReceipts

    val receipt: LiveData<Resource<Receipt?>>
        get() = _receipt

    val paidReceipt: LiveData<Resource<Receipt>>
        get() = _paidReceipt

    val normalizedReceipt: LiveData<Resource<Receipt>>
        get() = _normalizedReceipt

    /*
    val receiptPrintMessage : LiveData<Resource<ResponseBody>>
        get() = _receiptPrintMessage
    */

    val receiptSaved: LiveData<Resource<Receipt?>>
        get() = _receiptSaved

    val lineItemsWithTaxes: LiveData<Resource<List<LineItemWithTaxes>>>
        get() = _lineItemsWithTaxes

    val lineItemWithTaxes: LiveData<Resource<LineItemWithTaxes?>>
        get() = _lineItemWithTaxes



    fun getUsers() = viewModelScope.launch {
        _users.value = Resource.Loading
        _users.value = repository.getUsers()
    }

    fun getLoggedInUser(id: String) = viewModelScope.launch {
        _user.value = Resource.Loading
        _user.value = repository.getUser(id)
    }

    fun registerUser(id: String, userReadDto: Employee) = viewModelScope.launch {
        _user.value = Resource.Loading
        repository.registerUser(id,userReadDto)
        _user.value = repository.getUser(id)
    }


    fun selectedWorkstation(workstation: Workstation?) = viewModelScope.launch {
        _selectedWorkstation.value = Resource.Loading
        _selectedWorkstation.value = repository.selectedWorkstation(workstation)
    }


    fun getWorkstations() = viewModelScope.launch {
        _workstations.value = Resource.Loading
        _workstations.value = repository.getWorkstations()
    }


    fun registerWorkstation(
            workstationId: String?,
            workstation: Workstation
    ) = viewModelScope.launch {
        _workstation.value = Resource.Loading
        _workstation.value = repository.registerWorkstation(workstationId, workstation)

        _workstations.value = Resource.Loading
        _workstations.value = repository.getWorkstations()
    }


    fun getWorkstation(workstation_id : String) = viewModelScope.launch {
        _workstation.value = Resource.Loading
        _workstation.value = repository.getWorkstation(workstation_id)
    }

    fun getAllCategories() = viewModelScope.launch {
        _allCategories.value = Resource.Loading
        _allCategories.value = repository.getAllCategories()
    }

    fun getCategoriesAvailable4Sale() = viewModelScope.launch {
        _categoriesAvailable4Sale.value = Resource.Loading
        _categoriesAvailable4Sale.value = repository.getCategoriesAvailable4Sale()
    }


    fun selectedCategory(category: Category?) = viewModelScope.launch {
        _selectedCategory.value = Resource.Loading
        _selectedCategory.value = repository.selectedCategory(category)
    }


    fun saveCategory(
            categoryId: String,
            category: Category
    ) = viewModelScope.launch {
        _category.value = Resource.Loading
        _category.value = repository.saveCategory(categoryId, category)

        _allCategories.value = Resource.Loading
        _allCategories.value = repository.getAllCategories()
    }



    fun getCategory(category_id:String) = viewModelScope.launch {
        _category.value = Resource.Loading
        _category.value = repository.getCategory(category_id)
    }

    fun getAllItems() = viewModelScope.launch {
        _allItems.value = Resource.Loading
        _allItems.value = repository.getAllItems()
    }

    fun itemsFromCategory(categoryId: String) = viewModelScope.launch {
        _itemsFromCategory.value = Resource.Loading
        _itemsFromCategory.value = repository.getItemsOfCategory(categoryId)
    }

    fun getVariants() = viewModelScope.launch {
        _variants.value = Resource.Loading
        _variants.value = repository.getVariants()
    }

    fun getStores() = viewModelScope.launch {
        _stores.value = Resource.Loading
        _stores.value = repository.getStores()
    }

    fun getSuppliers() = viewModelScope.launch {
        _suppliers.value = Resource.Loading
        _suppliers.value = repository.getSuppliers()
    }

    fun setOrder(order:Order?) = viewModelScope.launch {
        _selectedOrder.value = Resource.Loading
        _selectedOrder.value = repository.setOrder(order)
    }

    fun getOrder(orderId: String) = viewModelScope.launch {
        _selectedOrder.value = Resource.Loading
        _selectedOrder.value = repository.getOrder(orderId)
    }

    fun getOrders() = viewModelScope.launch {
        _orders.value = Resource.Loading
        _orders.value = repository.getOrders()
    }

    fun registerOrder(order: Order) = viewModelScope.launch {
        _registeredOrder.value = Resource.Loading
        _registeredOrder.value = repository.registerOrder(order)
        _orders.value = repository.getOrders()
    }

    fun deleteOrder(order: Order) = viewModelScope.launch {
        _orders.value = Resource.Loading
        repository.deleteOrder(order.id!!)
        _orders.value = repository.getOrders()
    }

    fun releaseRegisteredOrder() = viewModelScope.launch {
        _registeredOrder.value = null
    }

    fun setOrderLine(orderLine: OrderLine?) = viewModelScope.launch {
        _selectedOrderLine.value = Resource.Loading
        _selectedOrderLine.value = repository.setOrderLine(orderLine)
    }


    fun updateOrderLine(orderLine: OrderLine) = viewModelScope.launch {
        _registeredOrderLine.value = Resource.Loading
        _registeredOrderLine.value = repository.updateOrderLine(orderLine)
    }


    fun getReceipts() = viewModelScope.launch {
        _receipts.value = Resource.Loading
        _receipts.value = repository.getReceipts()
        _receipt.value = null
    }


    fun getReceiptsAtTheDateOf(date : String) = viewModelScope.launch {
        _receipts.value = Resource.Loading
        _receipts.value = repository.getReceiptsAtTheDateOf(date)
    }


    fun getReceiptsOfWaiter(date : String?) = viewModelScope.launch {
        _myReceipts.value = Resource.Loading
        _receipt.value = null
        _myReceipts.value = repository.getReceiptsOfWaiter(date)
    }


    fun selectReceiptIds(selectedReceiptIds: List<String>) = viewModelScope.launch {
        _selectedReceiptIds.value = Resource.Loading
        _selectedReceiptIds.value = repository.selectReceiptIds(selectedReceiptIds)
    }


    fun releaseSavedReceipt() = viewModelScope.launch {
        _receiptSaved.value = null
    }

    fun saveReceipt(receipt: Receipt) = viewModelScope.launch {
        _receiptSaved.value = Resource.Loading
        _receiptSaved.value = repository.saveReceipt(receipt)
        //
        truncateLineItemsFromRoom()
        releaseSavedReceipt()
        _receipts.value = repository.getReceipts()
    }

    fun divideReceipt(id: String?, receiptsSourceDestination: ReceiptsSourceDestination) = viewModelScope.launch {
        _receiptSourceDestination.value = Resource.Loading
        _receiptSourceDestination.value = repository.divideReceipt(id, receiptsSourceDestination)
    }

    fun mergeReceipts(id: String?, receiptIds: List<String>) = viewModelScope.launch {
        _mergedReceipt.value = Resource.Loading
        _mergedReceipt.value = repository.mergeReceipts(id, receiptIds)
    }

    fun clearMergeReceipts() = viewModelScope.launch {
        _mergedReceipt.value = null
    }

    fun clearForwardedReceipts() = viewModelScope.launch {
        _forwardedReceipts.value = null
    }

    fun forwardReceipts(id: String, receiptIds: List<String>) = viewModelScope.launch {
        _forwardedReceipts.value = Resource.Loading
        _forwardedReceipts.value = repository.forwardReceipts(id, receiptIds)
    }

    fun cancelReceipts(receiptIds: List<String>) = viewModelScope.launch {
        _cancelledReceipts.value = Resource.Loading
        _cancelledReceipts.value = repository.cancelReceipts(receiptIds)
    }

    fun closeReceipt(receiptId: String?) = viewModelScope.launch {
        repository.closeReceipt(receiptId)
    }

    fun payReceipt(receiptId: String?,payments: List<Payment>) = viewModelScope.launch {
        _paidReceipt.value = Resource.Loading
        _paidReceipt.value = repository.payReceipt(receiptId, payments)
    }

    fun declareReceipt(receiptId: String?) = viewModelScope.launch {
        _normalizedReceipt.value = Resource.Loading
        _normalizedReceipt.value = repository.declareReceipt(receiptId)
    }

    /*
    fun printReceipt(receiptId: String?) = viewModelScope.launch {
        _receiptPrintMessage.value = Resource.Loading
        _receiptPrintMessage.value = repository.printReceipt(receiptId)
    }
    */

    fun getReceipt(receiptId:String?) = viewModelScope.launch {
        _receiptId = receiptId
        _receipt.value = Resource.Loading
        _receipt.value = repository.getReceipt(receiptId)
    }

    fun setReceipt(receipt: Receipt?) = viewModelScope.launch {
        _selectedReceipt.value = Resource.Loading
        _selectedReceipt.value = repository.setReceipt(receipt)
    }

    fun refreshSelectedReceipt() = viewModelScope.launch {
        try {
            Log.d(TAG, "refreshSelectedReceipt: receiptId = ${_receiptId}")
            _receipt.value = Resource.Loading
            _receipt.value = repository.getReceipt(_receiptId)
        }catch (ex : Exception){
            
        }
    }

    fun selectDiningOption(diningOption:String) = viewModelScope.launch {
        _diningOption.value = Resource.Loading
        _diningOption.value = repository.selectDiningOption(diningOption)
    }


    fun selectReceipt(receipt:Receipt) = viewModelScope.launch {
        _receipt.value = Resource.Loading
        _receipt.value = repository.selectReceipt(receipt)
    }


    fun selectReceiptSource(source:Receipt) = viewModelScope.launch {
        _receiptSource.value = Resource.Loading
        _receiptSource.value = repository.selectReceiptSource(source)
    }

    fun selectReceiptDestination(destination:Receipt) = viewModelScope.launch {
        _receiptDestination.value = Resource.Loading
        _receiptDestination.value = repository.selectReceiptDestination(destination)
    }

    fun transfertLineItem2Source(lineItems: List<LineItem>) = viewModelScope.launch {
        _receiptSource.value = Resource.Loading
        _receiptDestination.value = Resource.Loading

        _receiptSource.value = repository.addLineItem2Source(lineItems)
        _receiptDestination.value = repository.removeLineItemFromDestination(lineItems)
    }

    fun transfertLineItem2Destination(lineItems: List<LineItem>) = viewModelScope.launch {
        _receiptSource.value = Resource.Loading
        _receiptDestination.value = Resource.Loading

        _receiptDestination.value = repository.addLineItem2Destination(lineItems)
        _receiptSource.value = repository.removeLineItemFromSource(lineItems)
    }


    fun selectLineItem(lineItemWithTaxes: LineItemWithTaxes) = viewModelScope.launch {
        _lineItemWithTaxes.value = Resource.Loading
        _lineItemWithTaxes.value = repository.selectLineItemWithTaxes(lineItemWithTaxes)
    }

    fun selectLineItem(lineItem: LineItem) = viewModelScope.launch {
        _lineItemWithTaxes.value = Resource.Loading
        _lineItemWithTaxes.value = repository.selectLineItemWithTaxes(lineItem.toLineItemWithTaxes())
    }

    fun releaseLineItem() = viewModelScope.launch {
        _lineItemWithTaxes.value = Resource.Loading
        _lineItemWithTaxes.value = null
    }

    fun saveLineItemsToRoom(lineItem: LineItem) = viewModelScope.launch {
        _lineItemsWithTaxes.value = Resource.Loading
        repository.saveLineItemsToRoom(lineItem)
        _lineItemsWithTaxes.value = repository.getLineItemWithTaxesFromRoom()
    }

    fun saveLineItemsToRoom(lineItems : List<LineItem>) = viewModelScope.launch {
        _lineItemsWithTaxes.value = Resource.Loading
        repository.saveLineItemsToRoom(lineItems)
        _lineItemsWithTaxes.value = repository.getLineItemWithTaxesFromRoom()
    }

    fun getLineItemWithTaxesFromRoom() = viewModelScope.launch {
        _lineItemsWithTaxes.value = Resource.Loading
        _lineItemsWithTaxes.value = repository.getLineItemWithTaxesFromRoom()
    }

    fun truncateLineItemsFromRoom() = viewModelScope.launch {
        _lineItemsWithTaxes.value = Resource.Loading
        repository.truncateLineItemsFromRoom()
        _lineItemsWithTaxes.value = repository.getLineItemWithTaxesFromRoom()
    }

    fun removeLineItemsFromRoom(lineItem: LineItem) = viewModelScope.launch {
        _lineItemsWithTaxes.value = Resource.Loading
        repository.removeLineItemsFromRoom(lineItem)
        _lineItemsWithTaxes.value = repository.getLineItemWithTaxesFromRoom()
    }

    fun processLineItem(lineId : String,lineItem : LineItem) = viewModelScope.launch {
        repository.processLineItem(lineId,lineItem)
        refreshSelectedReceipt()
        _receipts.value = Resource.Loading
        _receipts.value = repository.getReceipts()
    }


}