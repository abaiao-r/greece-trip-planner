package dev.greecetripplanner.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomTemplateDao {
    @Query("SELECT * FROM custom_templates ORDER BY name ASC")
    fun observeAll(): Flow<List<CustomTemplateEntity>>

    @Query("SELECT * FROM custom_templates ORDER BY name ASC")
    suspend fun getAll(): List<CustomTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: CustomTemplateEntity)

    @Query("DELETE FROM custom_templates WHERE `key` = :key")
    suspend fun delete(key: String)
}
