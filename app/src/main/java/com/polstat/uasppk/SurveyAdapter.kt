package com.polstat.uasppk

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class SurveyAdapter : ListAdapter<Survey, SurveyAdapter.SurveyViewHolder>(SurveyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.survey_card, parent, false)
        return SurveyViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyViewHolder, position: Int) {
        val survey = getItem(position)
        holder.bind(survey)
    }

    override fun getItemCount(): Int = currentList.size

    inner class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val surveyTitle: TextView = itemView.findViewById(R.id.surveyTitle)
        private val surveyDescription: TextView = itemView.findViewById(R.id.surveyDescription)

        fun bind(survey: Survey) {
            surveyTitle.text = survey.title
            surveyDescription.text = survey.description

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, SurveyDetailActivity::class.java)
                intent.putExtra("SURVEY_ID", survey.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    // DiffCallback to optimize list updates
    class SurveyDiffCallback : DiffUtil.ItemCallback<Survey>() {
        override fun areItemsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Survey, newItem: Survey): Boolean {
            return oldItem == newItem
        }
    }
}

