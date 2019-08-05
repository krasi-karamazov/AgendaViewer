package kpk.dev.presentation.view.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_event.view.*
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.utils.DateUIUtils
import org.joda.time.DateTime
import org.zakariya.stickyheaders.SectioningAdapter


class EventsAdapter constructor(private val dateUIUtils: DateUIUtils): SectioningAdapter() {
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
        val headerDate = DateTime(sections[sectionIndex].dayInMillis)
        (viewHolder as DayViewHolder).tvDay.text = dateUIUtils.getDayNameByIndex(headerDate.dayOfWeek)
        viewHolder.tvDate.text = headerDate.toString("dd/MM/yyyy")
    }

    override fun onBindItemViewHolder(viewHolder: ItemViewHolder?, sectionIndex: Int, itemIndex: Int, itemUserType: Int) {
        val hexColor = String.format("#%06X", 0xFFFFFF and sections[sectionIndex].events[itemIndex].calendarColor)
        (viewHolder as EventViewHolder).calendarColor.setBackgroundColor(Color.parseColor(hexColor))
        viewHolder.title.text = sections[sectionIndex].events[itemIndex].title
        val eventDate = DateTime(sections[sectionIndex].events[itemIndex].stStart)
        viewHolder.tvWhen.text = eventDate.toString("dd/MM/yyyy HH:mm")
        viewHolder.tvWhere.text = sections[sectionIndex].events[itemIndex].location
    }

    private data class Section(val dayInMillis: Long, val events: MutableList<ScheduledEvent>)

    inner class EventViewHolder(itemView: View): SectioningAdapter.ItemViewHolder(itemView) {
        val calendarColor: View = itemView.agenda_item_color
        val title: TextView = itemView.tv_title
        val tvWhen: TextView = itemView.tv_when
        val tvWhere: TextView = itemView.tv_where
    }

    inner class DayViewHolder(itemView: View): SectioningAdapter.HeaderViewHolder(itemView) {
        val tvDay: TextView = itemView.tv_day
        val tvDate: TextView = itemView.tv_date
    }

}