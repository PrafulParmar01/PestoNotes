package com.pesto.task.management.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.pesto.task.management.R
import com.pesto.task.management.ui.tasks.TaskModel

class TaskListAdapter(val mContext: Context) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    private var mList: List<TaskModel> = arrayListOf()
    var onClickArrow: ((data: TaskModel) -> Unit) = { }
    var onEditClick: ((data: TaskModel) -> Unit) = { }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtTitle: AppCompatTextView = itemView.findViewById(R.id.txtTitle)
        val txtDescription: AppCompatTextView = itemView.findViewById(R.id.txtDescription)
        val txtDateTime: AppCompatTextView = itemView.findViewById(R.id.txtDate)
        val imgStatus: AppCompatImageView = itemView.findViewById(R.id.imgStatus)
        val btnDelete: AppCompatImageView = itemView.findViewById(R.id.btnDelete)
        val btnEdit: AppCompatImageView = itemView.findViewById(R.id.btnEdit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = mList[position]
        holder.txtTitle.text = info.title
        holder.txtDescription.text = info.description
        holder.txtDateTime.text = info.dateTime
        if (info.status == "0") {
            holder.imgStatus.setImageResource(R.drawable.ic_circle_progress)
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_circle_done)
        }

        holder.btnDelete.setOnClickListener {
            onClickArrow(mList[holder.adapterPosition])
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(mList[holder.adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_task_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun onUpdateList(taskList: List<TaskModel>) {
        mList = taskList
        notifyDataSetChanged()
    }
}