package com.amstrong.gaseapp.data.db.entities

import java.util.*

data class Workstation(
        var id: String?,
        val name: String,

        val canTakeOrders: Boolean,
        val canPrepareMealsOrDrinks: Boolean,
        val canCashIn: Boolean,

        val canManagePurchaseOrders: Boolean,
        val canManageHall: Boolean,
        val canManagekitchen: Boolean,
        val canManageUsers: Boolean,
        val canManageCategories: Boolean,
        val canManageWorkstations: Boolean,

        val created_at: Date,
) {

    var updated_at: Date? = null
    var deleted_at: Date? = null

    var employees: List<Employee> = listOf()

    constructor(
            id: String?,
            name: String,

            canTakeOrders: Boolean,
            canServeDrinksOrPrepareMeals: Boolean,
            canCashIn: Boolean,

            canManagePurchaseOrder: Boolean,
            canManageHall: Boolean,
            canManagekitchen: Boolean,
            canManageUsers: Boolean,
            canManageCategories: Boolean,
            canManageWorkstations: Boolean,

            created_at: Date,
            updated_at: Date?,
            deleted_at: Date?,

            employees: List<Employee>,
    ) : this(
            id,
            name,
            canTakeOrders,
            canServeDrinksOrPrepareMeals,
            canCashIn,
            canManagePurchaseOrder,
            canManageHall,
            canManagekitchen,
            canManageUsers,
            canManageCategories,
            canManageWorkstations,
            created_at,
    ) {
        this.updated_at = updated_at
        this.deleted_at = deleted_at
        this.employees = employees
    }
}