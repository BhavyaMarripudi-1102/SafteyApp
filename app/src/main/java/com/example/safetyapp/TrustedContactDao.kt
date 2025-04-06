package com.example.safetyapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrustedContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: TrustedContact)

    @Delete
    suspend fun delete(contact: TrustedContact)

    @Query("SELECT * FROM trusted_contacts")
    fun getAllContacts(): LiveData<List<TrustedContact>>
}