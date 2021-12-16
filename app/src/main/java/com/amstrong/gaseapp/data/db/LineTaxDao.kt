package com.amstrong.mvvmsampleproject.data.db

import androidx.room.*
import com.amstrong.gaseapp.data.db.entities.LineTax
import com.amstrong.gaseapp.data.db.entities.relations.LineItemWithTaxes

@Dao
interface LineTaxDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLineTaxsToSqlDB(lineTax: LineTax) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLineTaxsToSqlDB(lineTaxs : List<LineTax>)


    @Query("DELETE FROM lineTax")
    suspend fun deleteAllLineTaxsOfSqlDB()

    @Delete
    suspend fun removeLineTaxFromSqlDB(lineTax : LineTax)


    @Delete
    suspend fun deleteLineTaxsFromSqlDB(lineTaxs : List<LineTax>)


    @Transaction
    @Query("SELECT * FROM lineTax")
    fun getLineTaxsFromSqLite() : List<LineTax>


}