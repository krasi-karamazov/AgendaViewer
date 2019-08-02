package kpk.dev.presentation.view.main

import android.Manifest
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.R
import kpk.dev.presentation.view.base.BaseActivity
import kpk.dev.presentation.view.main.adapter.DaySection
import org.zakariya.stickyheaders.StickyHeaderLayoutManager
import javax.inject.Inject

class MainActivity: BaseActivity() {

    @Inject
    internal lateinit var calendarContentResolver: CalendarContentResolver

    private val sectionedRecyclerViewAdapter: StickyHeaderSectionedRecyclerViewAdapter by lazy {
        StickyHeaderSectionedRecyclerViewAdapter()
    }


    override fun onResume() {
        super.onResume()
        var calendarMap: MutableMap<Calendar, Map<String, MutableList<ScheduledEvent>>> = mutableMapOf()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            Observable.just(calendarContentResolver.getScheduleData(true))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { t ->
                        val daysMap: MutableMap<String, MutableList<ScheduledEvent>> = mutableMapOf()
                        val calendars = t.keys

                        for(calendar in calendars) {
                            val daysMapByCalendar = t[calendar]
                            for(day: String in daysMapByCalendar!!.keys) {
                                if(daysMap.containsKey(day)){
                                    daysMap[day]!!.addAll(daysMapByCalendar[day] ?: error(""))
                                } else {
                                    daysMap[day] = daysMapByCalendar[day] ?: error("")
                                }
                            }
                        }

                        return@map daysMap
                    }.doOnNext{t ->
                        setUpAdapterData(t)
                    }

                    .subscribe()

        }else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 666)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR), 667)
        }
    }

    private fun setUpAdapterData(daysMap: MutableMap<String, MutableList<ScheduledEvent>>) {
        rv_events_by_day.layoutManager = StickyHeaderLayoutManager()
        for(day in daysMap.keys) {
            sectionedRecyclerViewAdapter.addSection(DaySection(day, daysMap[day]!!))
        }
        rv_events_by_day.adapter = sectionedRecyclerViewAdapter
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