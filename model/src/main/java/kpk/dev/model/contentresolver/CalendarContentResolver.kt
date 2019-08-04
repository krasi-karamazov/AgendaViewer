package kpk.dev.model.contentresolver

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class CalendarContentResolver @Inject constructor(val contentResolver: ContentResolver) {

    companion object {
        private const val DAY_IN_MILLIS = 86400000
    }

    private val calendarsProjection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE)

    private val eventsProjection = arrayOf(CalendarContract.Instances._ID, CalendarContract.Instances.TITLE, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.DURATION, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.CALENDAR_COLOR, CalendarContract.Instances.CALENDAR_DISPLAY_NAME, CalendarContract.Instances.HAS_ATTENDEE_DATA, CalendarContract.Instances.RRULE, CalendarContract.Instances.RDATE)

    private val calendarsUri = CalendarContract.Calendars.CONTENT_URI


    private val eventsByCalendar: MutableMap<Calendar, Map<String, MutableList<ScheduledEvent>>> = mutableMapOf()

    fun getCalendars(): Set<Calendar> {
        val calendars = mutableSetOf<Calendar>()
        val cursor: Cursor? = contentResolver.query(calendarsUri, calendarsProjection, null, null, null)
        cursor.use { usedCursor ->
            usedCursor?.let {
                if(it.count > 0) {
                    while(it.moveToNext()) {
                        val id = it.getString(it.getColumnIndex(CalendarContract.Calendars._ID))
                        val name = it.getString(it.getColumnIndex(CalendarContract.Calendars.NAME))
                        val displayName = it.getString(it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                        val color = it.getString(it.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR))
                        val selected = it.getString(it.getColumnIndex(CalendarContract.Calendars.VISIBLE)) != "0"
                        val calendar = Calendar(id, name, displayName, color, selected)
                        calendars.add(calendar)
                    }
                }
            }
        }

        return calendars
    }

    fun getEventsInTimeSpan(fromDay: DateTime, toDay:DateTime): TreeMap<Long, MutableList<ScheduledEvent>> {
        val daysMap: TreeMap<Long, MutableList<ScheduledEvent>> = TreeMap()
        val instancesUriBuilder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(instancesUriBuilder, fromDay.millis)//now.millis - (DAY_IN_MILLIS * daysBeforeNow))
        ContentUris.appendId(instancesUriBuilder, toDay.millis)//now.millis + (DAY_IN_MILLIS * daysAfterNow))
        val instancesUri = instancesUriBuilder.build()
        val cursor: Cursor? = contentResolver.query(instancesUri, eventsProjection,  CalendarContract.Instances.VISIBLE + " = 1", null, CalendarContract.Instances.BEGIN)

        cursor.use { usedCursor ->
            usedCursor?.let {
                while (it.moveToNext()) {
                    val id = it.getInt(it.getColumnIndex(CalendarContract.Instances._ID))
                    val title = it.getString(it.getColumnIndex(CalendarContract.Instances.TITLE))
                    val description = it.getString(it.getColumnIndex(CalendarContract.Instances.DESCRIPTION)) ?: ""
                    val dtStart = it.getLong(it.getColumnIndex(CalendarContract.Instances.BEGIN))
                    val dtEnd = it.getLong(it.getColumnIndex(CalendarContract.Instances.END))
                    val allDay = it.getInt(it.getColumnIndex(CalendarContract.Instances.ALL_DAY))
                    val duration = it.getString(it.getColumnIndex(CalendarContract.Instances.DURATION)) ?: ""
                    val location = it.getString(it.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)) ?: ""
                    val calendarColor = it.getInt(it.getColumnIndex(CalendarContract.Instances.CALENDAR_COLOR))
                    val calendarDisplayName = it.getString(it.getColumnIndex(CalendarContract.Instances.CALENDAR_DISPLAY_NAME))
                    val hasAttendeeData = it.getInt(it.getColumnIndex(CalendarContract.Instances.HAS_ATTENDEE_DATA))
                    val rRule = it.getString(it.getColumnIndex(CalendarContract.Instances.RRULE))
                    val rDate = it.getString(it.getColumnIndex(CalendarContract.Instances.RDATE))

                    val scheduledEvent = ScheduledEvent(id, title, description, dtStart, dtEnd, (allDay == 1), duration, location, calendarColor, calendarDisplayName, (hasAttendeeData == 1), rRule, rDate)

                    val eventDay = DateTime(dtStart)
                    val dayKey: Long = eventDay.withHourOfDay(0).withMinuteOfHour(0).millis
                    if(daysMap.containsKey(dayKey)) {
                        daysMap[dayKey]?.add(scheduledEvent)
                    }else{
                        val eventsList: MutableList<ScheduledEvent> = mutableListOf()
                        eventsList.add(scheduledEvent)
                        daysMap[dayKey] = eventsList
                    }
                }
            }
        }

        return daysMap
    }

}