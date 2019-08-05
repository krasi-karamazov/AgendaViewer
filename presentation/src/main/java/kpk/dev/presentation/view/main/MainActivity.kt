package kpk.dev.presentation.view.main

import android.Manifest
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.utils.DateUIUtils
import kpk.dev.presentation.utils.EventsListUtils
import kpk.dev.presentation.view.base.BaseActivity
import kpk.dev.presentation.view.main.adapter.EventsAdapter
import kpk.dev.presentation.view.main.adapter.TwoWayEndlessRecyclerViewScrollListener
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

    private val sectionedRecyclerViewAdapter: EventsAdapter by lazy {
        EventsAdapter(dateUIUtils)
    }


    override fun onResume() {
        super.onResume()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            val now = DateTime.now()
            Observable.just(calendarContentResolver.getEventsInTimeSpan(now.minusDays(20), now.plusDays(20)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext{t ->
                        setUpAdapterData(t)
                    }

                    .subscribe()

        }else {
            //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 666)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR), 667)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.item_add_event -> {
                Observable.just(calendarContentResolver.getFirstFreeTimeSlot(CalendarContentResolver.EventDuration.HALFHOUR, DateTime.now().withDayOfMonth(6).withHourOfDay(12).withMinuteOfHour(0)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext{t ->

                        }

                        .subscribe()
            }
        }
        return true
    }

    private fun setUpAdapterData(daysMap: TreeMap<Long, MutableList<ScheduledEvent>>) {
        val layoutManager = StickyHeaderLayoutManager()
        rv_events_by_day.layoutManager = layoutManager
        sectionedRecyclerViewAdapter.addNewData(daysMap, olderDates = false, initial = true)
        rv_events_by_day.adapter = sectionedRecyclerViewAdapter
        rv_events_by_day.scrollToPosition(sectionedRecyclerViewAdapter.getAdapterPositionForSectionHeader(eventsListUtils.getPositionOfNearestGreaterOrEqualDayWithEvents(DateTime.now(), daysMap)))
        rv_events_by_day.addOnScrollListener(object : TwoWayEndlessRecyclerViewScrollListener(rv_events_by_day) {
            override fun onLoadNext() {
                addNewAdapterData(DateTime(sectionedRecyclerViewAdapter.getHeaderDataAtPosition(sectionedRecyclerViewAdapter.numberOfSections - 1 )).millis, false, false)
            }

            override fun onLoadPrevious() {
                addNewAdapterData(DateTime(sectionedRecyclerViewAdapter.getHeaderDataAtPosition(0 )).minusDays(20).millis, true, false)
            }
        })
    }

    fun addNewAdapterData(now: Long, olderDates: Boolean, initialLoad: Boolean) {
        val currentDate = DateTime(now)
        Observable.just(calendarContentResolver.getEventsInTimeSpan(currentDate, currentDate.plusDays(20)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{t ->
                    sectionedRecyclerViewAdapter.addNewData(t, olderDates, initialLoad)
                }

                .subscribe()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            666 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
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