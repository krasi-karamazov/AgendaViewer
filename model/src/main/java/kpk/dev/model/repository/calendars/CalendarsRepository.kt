package kpk.dev.model.repository.calendars

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.Calendar
import javax.inject.Inject

class CalendarsRepository @Inject constructor(val calendarContentResolver: CalendarContentResolver): ICalendarsRepository {

    override fun getCalendars(): Observable<Set<Calendar>> {
        return Observable.just(calendarContentResolver.getCalendars())
                .subscribeOn(Schedulers.io())

    }
}