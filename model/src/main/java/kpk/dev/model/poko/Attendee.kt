package kpk.dev.model.poko

data class Attendee(val id: Long, val eventId: Long, val attendeeName: String, val attendeeEmail: String, val attendeeType: Int, val attendeeRelationship: Int, val attendeeStatus: Int)