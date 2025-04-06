package com.example.safetyapp

class ContactRepository(private val dao: TrustedContactDao) {
    val allContacts = dao.getAllContacts()

    suspend fun insert(contact: TrustedContact) = dao.insert(contact)
    suspend fun delete(contact: TrustedContact) = dao.delete(contact)
}