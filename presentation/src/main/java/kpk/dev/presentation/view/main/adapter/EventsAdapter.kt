package kpk.dev.presentation.view.main.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_day_header.view.*
import kotlinx.android.synthetic.main.item_event.view.*
import kpk.dev.model.poko.ScheduledEvent
import org.zakariya.stickyheaders.SectioningAdapter

class EventsAdapter: SectioningAdapter() {

//https://github.com/ShamylZakariya/StickyHeaders/blob/master/app/src/main/java/org/zakariya/stickyheadersapp/adapters/AddressBookDemoAdapter.java
    data class Section(val day: String, val events: MutableList<ScheduledEvent>)

    inner class ItemViewHolder(itemView: View): SectioningAdapter.ItemViewHolder(itemView) {
        val tvEventName = itemView?.tv_event_name
    }

    inner class HeaderViewHolder(itemView: View): SectioningAdapter.HeaderViewHolder(itemView) {
        val tvDay = itemView?.tv_date
    }

}