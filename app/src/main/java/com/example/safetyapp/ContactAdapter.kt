package com.example.safetyapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val onDeleteClick: (TrustedContact) -> Unit
) : ListAdapter<TrustedContact, ContactAdapter.ContactViewHolder>(DiffCallback()) {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.contactName)
        val phoneText: TextView = itemView.findViewById(R.id.contactPhone)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete) // Add this in XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.nameText.text = contact.name
        holder.phoneText.text = contact.phoneNumber

        holder.deleteButton.setOnClickListener {
            onDeleteClick(contact)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TrustedContact>() {
        override fun areItemsTheSame(oldItem: TrustedContact, newItem: TrustedContact) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TrustedContact, newItem: TrustedContact) =
            oldItem == newItem
    }
}