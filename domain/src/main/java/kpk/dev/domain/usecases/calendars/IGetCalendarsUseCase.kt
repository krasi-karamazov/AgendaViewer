package kpk.dev.domain.usecases.calendars

import io.reactivex.Observable
import kpk.dev.model.poko.Calendar

interface IGetCalendarsUseCase {
    fun getCalendars(): Observable<Set<Calendar>>
}