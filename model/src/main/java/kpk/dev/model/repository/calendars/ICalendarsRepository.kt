package kpk.dev.model.repository.calendars

import io.reactivex.Observable
import io.reactivex.Single
import kpk.dev.model.poko.Calendar

interface ICalendarsRepository {
    fun getCalendars(): Observable<Set<Calendar>>
}