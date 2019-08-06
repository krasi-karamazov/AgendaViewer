package kpk.dev.presentation.view.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_event.view.*
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.utils.DateUIUtils
import org.joda.time.DateTime
import org.zakariya.stickyheaders.SectioningAdapter


class EventsAdapter constructor(private val dateUIUtils: DateUIUtils, val itemClickListener: (ScheduledEvent) -> Unit): SectioningAdapter() {
    private var sections: MutableList<Section> = mutableListOf()

    fun addNewData(data: Map<Long, MutableList<ScheduledEvent>>, olderDates:Boolean, initial: Boolean) {
        generateSections(data, olderDates, initial)
    }


    private fun generateSections(newData: Map<Long, MutableList<ScheduledEvent>>, olderDates: Boolean, initial:Boolean) {
        val newSections = mutableListOf<Section>()
        for(key in newData.keys) {
            newSections.add(Section(key, newData[key] ?: error("")))
        }
        if(initial){
            sections.addAll(newSections)

            notifyAllSectionsDataSetChanged()
            return
        }


        if(olderDates) {
            for(i in newSections.size - 1 downTo 0) {
                sections.add(0, newSections[i])
                notifySectionInserted(0)
            }
        }else {
            for(i in 0 until newSections.size - 1) {
                sections.add(newSections[i])
                notifySectionInserted(sections.size - 1)
            }
        }
    }

    fun getHeaderDataAtPosition(position: Int): Long = this.sections[position].dayInMillis

    override fun getNumberOfSections(): Int = sections.size

    override fun getNumberOfItemsInSection(sectionIndex: Int): Int = sections[sectionIndex].events.size

    override fun doesSectionHaveFooter(sectionIndex: Int): Boolean = false

    override fun doesSectionHaveHeader(sectionIndex: Int): Boolean = true

    @SuppressLint("InflateParams")
    override fun onCreateHeaderViewHolder(parent: ViewGroup?, headerUserType: Int): HeaderViewHolder = DayViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_day_header, parent, false))

    override fun onCreateItemViewHolder(parent: ViewGroup?, itemUserType: Int): ItemViewHolder = EventViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_event, parent, false))

    override fun onBindHeaderViewHolder(viewHolder: HeaderViewHolder?, sectionIndex: Int, headerUserType: Int) {
        (viewHolder as DayViewHolder).bind(sections[sectionIndex])
    }

    override fun onBindItemViewHolder(viewHolder: ItemViewHolder?, sectionIndex: Int, itemIndex: Int, itemUserType: Int) {
        (viewHolder as EventViewHolder).bind(sections[sectionIndex].events[itemIndex], this.itemClickListener)
    }

    data class Section(val dayInMillis: Long, val events: MutableList<ScheduledEvent>)

    inner class EventViewHolder(itemView: View): SectioningAdapter.ItemViewHolder(itemView) {
        fun bind(event: ScheduledEvent, listener: (ScheduledEvent) -> Unit) = with(itemView) {
            val hexColor = String.format("#%06X", 0xFFFFFF and event.calendarColor)
            itemView.agenda_item_color.setBackgroundColor(Color.parseColor(hexColor))
            itemView.tv_title.text = event.title
            val eventDate = DateTime(event.stStart)
            itemView.tv_when.text = eventDate.toString("dd/MM/yyyy HH:mm")
            itemView.tv_where.text = event.location
            itemView.setOnClickListener {listener(event)}
        }
    }

    inner class DayViewHolder(itemView: View): SectioningAdapter.HeaderViewHolder(itemView) {
        fun bind(section: Section) = with(itemView) {
            val headerDate = DateTime(section.dayInMillis)
            itemView.tv_day.text = dateUIUtils.getDayNameByIndex(headerDate.dayOfWeek)
            itemView.tv_date.text = headerDate.toString("dd/MM/yyyy")
        }
    }

}