package kpk.dev.presentation.view.main.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_event.view.*
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R

class DaySection(val day: String, private val events: MutableList<ScheduledEvent>): StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.item_event)
        .headerResourceId(R.layout.item_day_header)
        .build()) {
    override fun getContentItemsTotal(): Int = events.size

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as EventViewHolder).tvEventName?.text = events[position].title
    }

    override fun getItemViewHolder(view: View?): RecyclerView.ViewHolder = EventViewHolder(view)

    override fun getHeaderViewHolder(view: View?): RecyclerView.ViewHolder = DayViewHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        (holder as DayViewHolder).tvDate?.text = day
    }

    inner class DayViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val tvDate = itemView?.tv_date
    }

    inner class EventViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        val tvEventName = itemView?.tv_event_name
    }
}