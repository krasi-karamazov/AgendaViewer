package kpk.dev.model.repository.availability

import io.reactivex.Observable
import kpk.dev.model.contentresolver.CalendarContentResolver
import org.joda.time.DateTime

interface IAvailabilityRepository {
    fun getAvailibility(duration: CalendarContentResolver.EventDuration): Observable<DateTime>
}