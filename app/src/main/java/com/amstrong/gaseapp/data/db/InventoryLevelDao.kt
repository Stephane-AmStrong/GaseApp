package com.amstrong.mvvmsampleproject.data.db

//@Dao
//interface InventoryLevelDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveInventoryLevelToSqlDB(inventoryLevel: InventoryLevel) : Long
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun saveInventoryLevelsToSqlDB(inventoryLevels : List<InventoryLevel>)
//
//
//    @Query("DELETE FROM inventorylevel")
//    suspend fun deleteAllInventoryLevelsOfSqlDB()
//
//    @Delete
//    suspend fun deleteInventoryLevelsFromSqlDB(inventoryLevel : InventoryLevel)
//
//
//    @Delete
//    suspend fun deleteInventoryLevelsFromSqlDB(inventoryLevels : List<InventoryLevel>)
//
//
//    @Query("SELECT * FROM inventorylevel")
//    fun getInventoryLevelsFromSqLite() : List<InventoryLevel>
//
//    @Query("SELECT * FROM inventorylevel WHERE id=:variantId ")
//    fun getInventoryLevelFromSqLite(variantId: String): InventoryLevel
//
//}