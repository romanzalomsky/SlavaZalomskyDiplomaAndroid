package com.zalomsky.sportscore.features.bottom_container.person.admin_settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zalomsky.sportscore.R
import com.zalomsky.sportscore.domain.models.RoleModel
import com.zalomsky.sportscore.domain.models.User

class AdminUsersAdapter(
    private var users: List<User>,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<AdminUsersAdapter.AdminUserViewHolder>() {

    fun submitList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user, parent, false)
        return AdminUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.username
        holder.email.text = user.email
        holder.deleteButton.isEnabled = user.roleModel != RoleModel.ADMIN
        holder.deleteButton.alpha = if (user.roleModel == RoleModel.ADMIN) 0.4f else 1f
        holder.deleteButton.setOnClickListener { onDeleteClick(user) }
    }

    override fun getItemCount(): Int = users.size

    class AdminUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.userNameText)
        val email: TextView = view.findViewById(R.id.userEmailText)
        val deleteButton: Button = view.findViewById(R.id.deleteUserButton)
    }
}
