package kpk.dev.model.contentresolver

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import kpk.dev.model.poko.Attendee
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class CalendarContentResolver @Inject constructor(val contentResolver: ContentResolver) {

    enum class EventDuration{
        HALFHOUR, HOUR
    }

    private val calendarsProjection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR, CalendarContract.Calendars.VISIBLE)

    private val instancesProjection = arrayOf(CalendarContract.Instances._ID, CalendarContract.Instances.TITLE, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.ALL_DAY, CalendarContract.Instances.DURATION, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.CALENDAR_COLOR, CalendarContract.Instances.CALENDAR_DISPLAY_NAME, CalendarContract.Instances.HAS_ATTENDEE_DATA, CalendarContract.Instances.RRULE, CalendarContract.Instances.RDATE, CalendarContract.Instances.EVENT_ID)

    private val attendeesProjection = arrayOf(
            CalendarContract.Attendees._ID,
            CalendarContract.Attendees.EVENT_ID,
            CalendarContract.Attendees.ATTENDEE_NAME,
            CalendarContract.Attendees.ATTENDEE_EMAIL,
            CalendarContract.Attendees.ATTENDEE_TYPE,
            CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
            CalendarContract.Attendees.ATTENDEE_STATUS)

    private val calendarsUri = CalendarContract.Calendars.CONTENT_URI

    @SuppressLint("MissingPermission")
    fun getCalendars(): Set<Calendar> {
        val calendars = mutableSetOf<Calendar>()
        val cursor: Cursor? = contentResolver.query(calendarsUri, calendarsProjection, null, null, null)
        cursor.use { calendarsCursor ->
            calendarsCursor?.let {
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
        ContentUris.appendId(instancesUriBuilder, fromDay.millis)
        ContentUris.appendId(instancesUriBuilder, toDay.millis)
        val instancesUri = instancesUriBuilder.build()
        val cursor: Cursor? = contentResolver.query(instancesUri, instancesProjection,  CalendarContract.Instances.VISIBLE + " = 1", null, CalendarContract.Instances.BEGIN)

        cursor.use { eventsCursor ->
            eventsCursor?.let {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndex(CalendarContract.Instances._ID))
                    val title = it.getString(it.getColumnIndex(CalendarContract.Instances.TITLE)) ?: ""
                    val description = it.getString(it.getColumnIndex(CalendarContract.Instances.DESCRIPTION)) ?: ""
                    val dtStart = it.getLong(it.getColumnIndex(CalendarContract.Instances.BEGIN))
                    val dtEnd = it.getLong(it.getColumnIndex(CalendarContract.Instances.END))
                    val allDay = it.getInt(it.getColumnIndex(CalendarContract.Instances.ALL_DAY)) == 1
                    val duration = it.getString(it.getColumnIndex(CalendarContract.Instances.DURATION)) ?: ""
                    val location = it.getString(it.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION)) ?: ""
                    val calendarColor = it.getInt(it.getColumnIndex(CalendarContract.Instances.CALENDAR_COLOR))
                    val calendarDisplayName = it.getString(it.getColumnIndex(CalendarContract.Instances.CALENDAR_DISPLAY_NAME))
                    val hasAttendeeData = it.getInt(it.getColumnIndex(CalendarContract.Instances.HAS_ATTENDEE_DATA)) == 1
                    val rRule = it.getString(it.getColumnIndex(CalendarContract.Instances.RRULE))
                    val rDate = it.getString(it.getColumnIndex(CalendarContract.Instances.RDATE))
                    val eventId = it.getLong(it.getColumnIndex(CalendarContract.Instances.EVENT_ID))

                    val attendeesList: List<Attendee> = getAttendeesForEventId(eventId)

                    val scheduledEvent = ScheduledEvent(id, title, description, dtStart, dtEnd, allDay, duration, location, calendarColor, calendarDisplayName, hasAttendeeData, rRule, rDate, eventId, attendeesList)

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

    @SuppressLint("MissingPermission")
    private fun getAttendeesForEventId(eventId: Long): List<Attendee> {
        val attendeesUri: Uri = CalendarContract.Attendees.CONTENT_URI
        val query = "(" + CalendarContract.Attendees.EVENT_ID + " = ?)"
        val args = arrayOf(eventId.toString())
        val cursor: Cursor? = contentResolver.query(attendeesUri, attendeesProjection, query, args, null)
        val attendees = mutableListOf<Attendee>()
        cursor.use { attendeesCursor ->
            attendeesCursor?.let {
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndex(CalendarContract.Attendees._ID))
                    val eventIdForAttendee = it.getLong(it.getColumnIndex(CalendarContract.Attendees.EVENT_ID))
                    val attendeeName = it.getString(it.getColumnIndex(CalendarContract.Attendees.ATTENDEE_NAME))
                    val attendeeEmail = it.getString(it.getColumnIndex(CalendarContract.Attendees.ATTENDEE_EMAIL))
                    val attendeeType = it.getInt(it.getColumnIndex(CalendarContract.Attendees.ATTENDEE_TYPE))
                    val attendeeRelationship = it.getInt(it.getColumnIndex(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP))
                    val attendeeStatus = it.getInt(it.getColumnIndex(CalendarContract.Attendees.ATTENDEE_STATUS))
                    val attendee = Attendee(id, eventIdForAttendee, attendeeName, attendeeEmail, attendeeType, attendeeRelationship, attendeeStatus)
                    attendees.add(attendee)
                }
            }
        }
        return attendees
    }

    fun getFirstFreeTimeSlot(duration: EventDuration, currentTime: DateTime): DateTime {
        var now = if(currentTime.hourOfDay > 20) currentTime.plusDays(1).withHourOfDay(8).withMinuteOfHour(0) else if (currentTime.hourOfDay < 8) currentTime.withHourOfDay(8).withMinuteOfHour(0) else currentTime

        now = if(duration == EventDuration.HALFHOUR) {
            if(now.minuteOfHour in 1..29) {
                now.withMinuteOfHour(30)
            }else if(now.minuteOfHour > 30){
                now.plusHours(1).withMinuteOfHour(0)
            }else{
                now
            }
        }else{
            if(now.minuteOfHour < 30 ) {
                now.withMinuteOfHour(0)
            }else{
                now.withMinuteOfHour(0).plusHours(1)
            }
        }

        var availableSlot = getAvailableSlot(now, duration)
        while(availableSlot > 0) {
            now = DateTime(availableSlot)
            availableSlot = getAvailableSlot(now, duration)
        }
        return now
    }

    private fun getAvailableSlot(now: DateTime, duration: EventDuration): Long {
        val instancesUriBuilder: Uri.Builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        val toTime = now.plusMinutes(if(duration == EventDuration.HALFHOUR) 30 else 60)
        ContentUris.appendId(instancesUriBuilder, now.plusMinutes(1).millis)
        ContentUris.appendId(instancesUriBuilder, toTime.millis)
        val instancesUri = instancesUriBuilder.build()
        val cursor: Cursor? = contentResolver.query(instancesUri, instancesProjection,  CalendarContract.Instances.VISIBLE + " = 1 AND " + CalendarContract.Instances.AVAILABILITY + " = " + CalendarContract.Instances.AVAILABILITY_BUSY + " AND " + CalendarContract.Instances.ALL_DAY + " = 0", null, CalendarContract.Instances.BEGIN)
        var occupiedTime: Long = 0
        cursor.use { usedCursor ->
            usedCursor?.let {
                if(usedCursor.count > 0) {
                    usedCursor.moveToLast()
                    val dtEnd = it.getLong(it.getColumnIndex(CalendarContract.Instances.END))
                    occupiedTime = dtEnd
                }
            }
        }
        return occupiedTime
    }

}