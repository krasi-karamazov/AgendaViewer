package kpk.dev.presentation.view.eventdetails.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_attendee.view.*
import kpk.dev.model.poko.Attendee
import kpk.dev.presentation.R

class AttendeesAdapter constructor(val data: List<Attendee>?): RecyclerView.Adapter<AttendeesAdapter.AttendeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder = AttendeeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_attendee, null, false))

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        holder.bind(data?.get(position))
    }

    inner class AttendeeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(attendee: Attendee?) = with(itemView) {
            this.tv_attendee_name.text = if(TextUtils.isEmpty(attendee?.attendeeName)) attendee?.attendeeEmail else attendee?.attendeeName
        }
    }
}