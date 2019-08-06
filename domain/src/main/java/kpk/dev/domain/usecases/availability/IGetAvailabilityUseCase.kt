package kpk.dev.domain.usecases.availability

import io.reactivex.Observable
import kpk.dev.model.contentresolver.CalendarContentResolver
import org.joda.time.DateTime

interface IGetAvailabilityUseCase {
    fun getAvailableSlotForMeeting(duration: CalendarContentResolver.EventDuration): Observable<DateTime>
}