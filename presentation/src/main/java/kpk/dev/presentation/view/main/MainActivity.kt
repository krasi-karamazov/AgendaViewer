package kpk.dev.presentation.view.main

import android.Manifest
import android.content.pm.PackageManager

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
            Observable.just(calendarContentResolver.getEventsInTimeSpan(10, 10))
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

    private fun setUpAdapterData(daysMap: TreeMap<Long, MutableList<ScheduledEvent>>) {
        rv_events_by_day.layoutManager = StickyHeaderLayoutManager()
        sectionedRecyclerViewAdapter.data = daysMap
        rv_events_by_day.adapter = sectionedRecyclerViewAdapter

        rv_events_by_day.scrollToPosition(sectionedRecyclerViewAdapter.getAdapterPositionForSectionHeader(eventsListUtils.getPositionOfNearestGreaterOrEqualDayWithEvents(DateTime.now(), daysMap)))
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