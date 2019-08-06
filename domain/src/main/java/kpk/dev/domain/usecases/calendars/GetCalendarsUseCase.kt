package kpk.dev.domain.usecases.calendars

import io.reactivex.Observable
import io.reactivex.Single
import kpk.dev.model.poko.Calendar
import kpk.dev.model.repository.calendars.CalendarsRepository
import javax.inject.Inject

class GetCalendarsUseCase @Inject constructor(val repo: CalendarsRepository): IGetCalendarsUseCase {
    override fun getCalendars(): Observable<Set<Calendar>> = repo.getCalendars()
}