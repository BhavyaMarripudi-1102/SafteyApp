package com.example.safetyapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TrustedContactDatabase.getDatabase(application).contactDao()
    private val repository = ContactRepository(dao)

    val allContacts = repository.allContacts

    fun insert(contact: TrustedContact) = viewModelScope.launch {
        repository.insert(contact)
    }

    fun delete(contact: TrustedContact) = viewModelScope.launch {
        repository.delete(contact)
    }
}