package com.example.practica2.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.practica2.EditUserActivity
import com.example.practica2.R
import com.example.practica2.models.User
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(
    private val userList: MutableList<User>,
    private val listener: OnUserActionListener
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnUserActionListener {
        fun onEditClicked(user: User)
        fun onDeleteClicked(user: User)
        fun onNotifyClicked(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        holder.editButton.setOnClickListener {
            listener.onEditClicked(user)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClicked(user)
        }

        holder.notifyButton.setOnClickListener {
            listener.onNotifyClicked(user)
        }
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val userEmail: TextView = view.findViewById(R.id.userEmail)
        val editButton: Button = view.findViewById(R.id.editButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
        val notifyButton: Button = view.findViewById(R.id.notifyButton)
        private val db = FirebaseFirestore.getInstance()

        fun bind(user: User) {
            userName.text = user.name
            userEmail.text = user.email
        }
    }
}
