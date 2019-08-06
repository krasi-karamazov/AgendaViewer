package kpk.dev.model.repository.events

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kpk.dev.model.contentresolver.CalendarContentResolver
import kpk.dev.model.poko.ScheduledEvent
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class EventsRepository @Inject constructor(val calendarContentResolver: CalendarContentResolver): IGetEventsRepository {

    override fun getEvents(fromDay: DateTime, toDay: DateTime): Observable<TreeMap<Long, MutableList<ScheduledEvent>>> {

        return Observable.just(calendarContentResolver.getEventsInTimeSpan(fromDay, toDay))
                .subscribeOn(Schedulers.io())
    }
}