package kpk.dev.presentation.view.main.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.dialog.BaseDialog
import kpk.dev.presentation.dialog.MessageDialog
import kpk.dev.presentation.utils.DateUIUtils
import kpk.dev.presentation.utils.EventsListUtils
import kpk.dev.presentation.view.base.BaseActivity
import kpk.dev.presentation.view.eventdetails.EventDetailsFragment
import kpk.dev.presentation.view.main.adapter.EventsAdapter
import kpk.dev.presentation.view.main.adapter.TwoWayEndlessRecyclerViewScrollListener
import kpk.dev.presentation.view.main.viewmodel.MainViewModel
import kpk.dev.presentation.viewmodel.factory.ViewModelFactory
import org.joda.time.DateTime
import org.zakariya.stickyheaders.StickyHeaderLayoutManager
import java.util.*
import javax.inject.Inject

class MainActivity: BaseActivity() {

    @Inject
    internal lateinit var calendarContentResolver: CalendarContentResolver

    @Inject
    internal lateinit var dateUIUtils: DateUIUtils
    @Inject
    internal lateinit var eventsListUtils: EventsListUtils

    @Inject
    internal lateinit var vmFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    private var fromDay: DateTime = DateTime.now().minusDays(20)
    private var toDay: DateTime = DateTime.now().plusDays(20)
    private var initialLoad: Boolean = true
    private var olderDates: Boolean = false
    private lateinit var scrollListener: TwoWayEndlessRecyclerViewScrollListener
    private val calendarPermissionsRequestCode = 667
    private val menuHalfHourId = 30045
    private val menuOneHourId = 30046
    private lateinit var duration: CalendarContentResolver.EventDuration

    private val eventsObserver: Observer<TreeMap<Long, MutableList<ScheduledEvent>>> = Observer {
        sectionedRecyclerViewAdapter.addNewData(it, olderDates, initialLoad)
        if(it.size == 0) {
            scrollListener.noMoreItems()
        }
        setupList()
        if(initialLoad) {
            rv_events_by_day.scrollToPosition(sectionedRecyclerViewAdapter.getAdapterPositionForSectionHeader(eventsListUtils.getPositionOfNearestGreaterOrEqualDayWithEvents(DateTime.now(), it)))
        }
    }

    private val availabilityObserver: Observer<DateTime> = Observer {
        val args = Bundle()
        val title: String = when(duration) {
            CalendarContentResolver.EventDuration.HALFHOUR -> getString(R.string.half_hour_availability)
            else -> getString(R.string.one_hour_availability)
        }

        args.putString(BaseDialog.TITLE_ARG_KEY, title)
        args.putString(BaseDialog.MESSAGE_ARG_KEY, "Your next available slot is " + it.toString("dd/MM") + " at " + it.toString("HH:mm"))
        MessageDialog.getInstance(args).show(supportFragmentManager, MessageDialog::class.java.simpleName)
    }

    private val sectionedRecyclerViewAdapter: EventsAdapter by lazy {
        EventsAdapter(dateUIUtils) { event ->
            run {
                val args = Bundle()
                args.putParcelable(EventDetailsFragment.EVENT_ARG_KEY, event)
                EventDetailsFragment.getInstance(args).show(supportFragmentManager, EventDetailsFragment::class.java.simpleName)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, menuHalfHourId, 1, getString(R.string.half_hour_schedule))
        menu?.add(0, menuOneHourId, 2, getString(R.string.one_hour_schedule))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            menuHalfHourId -> duration = CalendarContentResolver.EventDuration.HALFHOUR

            menuOneHourId -> duration = CalendarContentResolver.EventDuration.HOUR
        }
        viewModel.getAvailableSlotForMeeting(duration).observe(this, availabilityObserver)
        return true
    }

    override fun init() {
        viewModel = vmFactory.get()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            addNewAdapterData(fromDay, toDay, false, true)
        }else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR), calendarPermissionsRequestCode)
        }
    }

    private fun setupList() {
        if(rv_events_by_day.adapter == null) {
            val layoutManager = StickyHeaderLayoutManager()
            rv_events_by_day.layoutManager = layoutManager
            rv_events_by_day.adapter = sectionedRecyclerViewAdapter
            scrollListener = object : TwoWayEndlessRecyclerViewScrollListener(rv_events_by_day) {
                override fun onLoadNext() {
                    fromDay = DateTime(sectionedRecyclerViewAdapter.getHeaderDataAtPosition(sectionedRecyclerViewAdapter.numberOfSections - 1 )).plusDays(1)
                    toDay = fromDay.plusDays(20)
                    addNewAdapterData(fromDay, toDay, false, false)
                }

                override fun onLoadPrevious() {
                    fromDay = DateTime(sectionedRecyclerViewAdapter.getHeaderDataAtPosition(0 )).minusDays(20)
                    toDay = fromDay.plusDays(20)
                    addNewAdapterData(fromDay, toDay, true, false)
                }
            }
            rv_events_by_day.addOnScrollListener(scrollListener)
        }
    }

    private fun addNewAdapterData(startDay: DateTime, endDay: DateTime , olderDates: Boolean, initialLoad: Boolean) {
        this.olderDates = olderDates
        this.initialLoad = initialLoad
        viewModel.getEvents(startDay, endDay).observe(this, eventsObserver)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            calendarPermissionsRequestCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addNewAdapterData(fromDay, toDay, false, true)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main
}