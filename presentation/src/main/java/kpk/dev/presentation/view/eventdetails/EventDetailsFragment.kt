package kpk.dev.presentation.view.eventdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_event_details.view.*
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.utils.DateUIUtils_Factory
import kpk.dev.presentation.view.eventdetails.adapter.AttendeesAdapter

class EventDetailsFragment: DialogFragment() {

    companion object {
        const val EVENT_ARG_KEY = "event"

        fun getInstance(args: Bundle?): EventDetailsFragment {
            val eventDetailsFragment = EventDetailsFragment()
            eventDetailsFragment.arguments = args
            return eventDetailsFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_event_details, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        val event: ScheduledEvent? = arguments?.getParcelable(EVENT_ARG_KEY)

        event?.let {
            view.tv_event_title.text = event.title
            val timeSTring = DateUIUtils_Factory.newInstance().getTimeStringForEvent(event.stStart, event.dtEnd, event.allDay)
            view.tv_time.text = timeSTring
            view.tv_event_location.text = event.location
            view.tv_description.text = event.description
            view.iv_close.setOnClickListener { dismiss() }
            val adapter = AttendeesAdapter(event.attendees)
            view.rv_attendees.adapter = adapter
            view.rv_attendees.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            ViewCompat.setNestedScrollingEnabled(view.rv_attendees, true)
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }
}