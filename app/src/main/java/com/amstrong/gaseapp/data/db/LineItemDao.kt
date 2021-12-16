package com.amstrong.mvvmsampleproject.data.db

import androidx.room.*
import com.amstrong.gaseapp.data.db.entities.LineItem
import com.amstrong.gaseapp.data.db.entities.LineTax
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes

@Dao
interface LineItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLineItemsToRoom(lineItem: LineItem) : Long

    @Insert
    fun saveLineTaxesToRoom(lineTaxes: List<LineTax>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLineItemsToRoom(lineItems : List<LineItem>)

    @Query("DELETE FROM lineitem")
    suspend fun truncateLineItemsFromRoom()

    @Query("DELETE FROM linetax")
    suspend fun truncateLineTaxesFromRoom()


    @Delete
    suspend fun removeLineItemsFromRoom(lineItem : LineItem)

    @Delete
    suspend fun removeLineItemsFromRoom(lineItems : List<LineItem>)

    @Delete
    suspend fun removeLineTaxesFromRoom(lineTaxes : List<LineTax>)

//
//    @Transaction
//    @Query("SELECT * FROM lineitem")
//    fun getLineItemsFromSqLite() : List<LineItem>
//

    @Transaction
    @Query("SELECT * FROM LineItem")
    fun getLineItemWithTaxesFromRoom(): List<LineItemWithTaxes>

}