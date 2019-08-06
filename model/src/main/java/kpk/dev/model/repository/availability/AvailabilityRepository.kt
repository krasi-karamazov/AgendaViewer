package kpk.dev.model.repository.availability

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kpk.dev.model.contentresolver.CalendarContentResolver
import org.joda.time.DateTime
import javax.inject.Inject

class AvailabilityRepository @Inject constructor(val calendarContentResolver: CalendarContentResolver): IAvailabilityRepository {
    override fun getAvailibility(duration: CalendarContentResolver.EventDuration): Observable<DateTime> {
        return Observable.just(calendarContentResolver.getFirstFreeTimeSlot(duration, DateTime.now()))
                .subscribeOn(Schedulers.io())
    }

}