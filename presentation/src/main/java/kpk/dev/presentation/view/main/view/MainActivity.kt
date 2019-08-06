package kpk.dev.presentation.view.main.view

import android.Manifest
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.utils.DateUIUtils
import kpk.dev.presentation.utils.EventsListUtils
import kpk.dev.presentation.utils.SingleLiveEvent
import kpk.dev.presentation.view.base.BaseActivity
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

    private var calendars: Set<Calendar>? = null
    private lateinit var eventsLiveData: SingleLiveEvent<TreeMap<Long, MutableList<ScheduledEvent>>>
    private var fromDay: DateTime = DateTime.now().minusDays(20)
    private var toDay: DateTime = DateTime.now().plusDays(20)
    private var initialLoad: Boolean = true
    private var olderDates: Boolean = false

    private val initialObserver: Observer<TreeMap<Long, MutableList<ScheduledEvent>>> = Observer {
        sectionedRecyclerViewAdapter.addNewData(it, olderDates, initialLoad)
        setupList()
        if(initialLoad) {
            rv_events_by_day.scrollToPosition(sectionedRecyclerViewAdapter.getAdapterPositionForSectionHeader(eventsListUtils.getPositionOfNearestGreaterOrEqualDayWithEvents(DateTime.now(), it)))
        }
    }

    private val sectionedRecyclerViewAdapter: EventsAdapter by lazy {
        EventsAdapter(dateUIUtils) { event -> {}}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private val CALENDAR_PERMISSIONS_REQUEST_CODE = 667

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_add_event -> {
                /*Observable.just(calendarContentResolver.getFirstFreeTimeSlot(CalendarContentResolver.EventDuration.HALFHOUR, DateTime.now().withDayOfMonth(6).withHourOfDay(12).withMinuteOfHour(0)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext{t ->

                        }

                        .subscribe()*/
            }
        }
        return true
    }

    override fun init() {
        viewModel = vmFactory.get()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            addNewAdapterData(fromDay, toDay, false, true)
        }else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR), CALENDAR_PERMISSIONS_REQUEST_CODE)
        }
    }

    fun setupList() {
        if(rv_events_by_day.adapter == null) {
            val layoutManager = StickyHeaderLayoutManager()
            rv_events_by_day.layoutManager = layoutManager
            rv_events_by_day.adapter = sectionedRecyclerViewAdapter

            rv_events_by_day.addOnScrollListener(object : TwoWayEndlessRecyclerViewScrollListener(rv_events_by_day) {
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
            })
        }
    }

    fun addNewAdapterData(startDay: DateTime, endDay: DateTime , olderDates: Boolean, initialLoad: Boolean) {
        this.olderDates = olderDates
        this.initialLoad = initialLoad
        viewModel.getEvents(startDay, endDay).observe(this, initialObserver)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            CALENDAR_PERMISSIONS_REQUEST_CODE -> {
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