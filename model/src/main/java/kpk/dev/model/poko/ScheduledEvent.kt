package kpk.dev.model.poko

data class ScheduledEvent(val id: Long, val title: String, val description: String, val stStart: Long, val dtEnd: Long, val allDay: Boolean, val duration: String, val location: String, val calendarColor: Int, val calendarDisplayName: String, val hasAttendeeData: Boolean, val rRule: String?, val rDate: String?, val eventId: Long, val attendees: List<Attendee>?)