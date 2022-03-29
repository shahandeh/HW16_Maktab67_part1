package com.example.hw16_maktab67_part1.ui.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hw16_maktab67_part1.R
import com.example.hw16_maktab67_part1.database.Task

class RecycleViewAdapter(private val listOfTask : List<Task>, private val clickListener: RecyclerViewListener) : RecyclerView.Adapter<RecycleViewAdapter.RecycleViewViewHolder>() {

    inner class RecycleViewViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        override fun onClick(p0: View?) {
            clickListener.recyclerViewClick(listOfTask[adapterPosition], adapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
        }

        private val icon: TextView = itemView.findViewById(R.id.task_model_icon)
        private val title: TextView = itemView.findViewById(R.id.task_model_title)
        private val time: TextView = itemView.findViewById(R.id.task_model_time)
        private val date: TextView = itemView.findViewById(R.id.task_model_date)

        fun bind(task: Task){
            icon.text = task.title[0].toString().uppercase()
            title.text = task.description
            time.text = task.time
            date.text = task.date
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_sample_model, parent, false)
        return RecycleViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecycleViewViewHolder, position: Int) {
        holder.bind(listOfTask[position])
    }

    override fun getItemCount(): Int {
        return listOfTask.size
    }

}