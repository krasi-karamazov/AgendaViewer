package kpk.dev.model.contentresolver

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class CalendarContentResolver @Inject constructor(val contentResolver: ContentResolver) {

    companion object {
        private const val DAY_IN_MILLIS = 86400000
    }

    private val CALENDARS_PROJECTION = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE)

    private val EVENTS_PROJECTION = arrayOf(CalendarContract.Instances._ID, CalendarContract.Instances.TITLE, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.DURATION, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.CALENDAR_COLOR, CalendarContract.Instances.CALENDAR_DISPLAY_NAME, CalendarContract.Instances.HAS_ATTENDEE_DATA, CalendarContract.Instances.RRULE, CalendarContract.Instances.RDATE)

    private val calendarsUri = CalendarContract.Calendars.CONTENT_URI


    private val eventsByCalendar: MutableMap<Calendar, Map<String, MutableList<ScheduledEvent>>> = mutableMapOf()

    fun getScheduleData(initialLoad: Boolean): MutableMap<Calendar, Map<String, MutableList<ScheduledEvent>>> {
        val cursor: Cursor? = contentResolver.query(calendarsUri, CALENDARS_PROJECTION, null, null, null)
        try{
            cursor?.let {
                if(it.count > 0) {
                    while(it.moveToNext()) {
                        val id = it.getString(it.getColumnIndex(CalendarContract.Calendars._ID))
                        val name = it.getString(it.getColumnIndex(CalendarContract.Calendars.NAME))
                        val displayName = it.getString(it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
                        val color = it.getString(it.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR))
                        val selected = it.getString(it.getColumnIndex(CalendarContract.Calendars.VISIBLE)) != "0"
                        val calendar = Calendar(id, name, displayName, color, selected)
                        eventsByCalendar[calendar] = getScheduledEventsByCalendarId(id)
                    }

                }
            }
        }finally {
            cursor?.close()
        }

        return eventsByCalendar
    }

    private fun getScheduledEventsByCalendarId(calendarId: String): Map<String, MutableList<ScheduledEvent>> {
        val daysMap: MutableMap<String, MutableList<ScheduledEvent>> = mutableMapOf()
        val now: DateTime = DateTime.now()
        val instancesUriBuilder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(instancesUriBuilder, now.millis - (DAY_IN_MILLIS * 10))
        ContentUris.appendId(instancesUriBuilder, now.millis + (DAY_IN_MILLIS * 10))
        val instancesUri = instancesUriBuilder.build()
        val calendarIdColumn = CalendarContract.Instances.CALENDAR_ID
        val cursor: Cursor? = contentResolver.query(instancesUri, EVENTS_PROJECTION, calendarIdColumn + "=" + calendarId + " and " + CalendarContract.Instances.VISIBLE + " = 1", null, null)

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
                    val dayKey = "${eventDay.dayOfMonth().get()}/${eventDay.monthOfYear().get()}/${eventDay.year().get()}"
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