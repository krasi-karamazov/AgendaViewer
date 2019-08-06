package kpk.dev.presentation.view.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kpk.dev.domain.usecases.calendars.GetCalendarsUseCase
import kpk.dev.domain.usecases.events.GetEventsUseCase
import kpk.dev.model.poko.Calendar
import kpk.dev.model.poko.ScheduledEvent
import kpk.dev.presentation.utils.SingleLiveEvent
import kpk.dev.presentation.viewmodel.base.BaseViewModel
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(val calendarsUseCase: GetCalendarsUseCase, val eventsUseCase: GetEventsUseCase, override var compositeDisposable:CompositeDisposable): BaseViewModel(compositeDisposable) {
    private var calendars: Set<Calendar>? = null
    private val calendarsData: SingleLiveEvent<Set<Calendar>> = SingleLiveEvent()
    private val eventsLiveData: MutableLiveData<TreeMap<Long, MutableList<ScheduledEvent>>> = MutableLiveData()

    fun getCalendars(): SingleLiveEvent<Set<Calendar>> {
        if(calendars != null) {
            calendarsData.postValue(calendars)
        }else{
            compositeDisposable.add(calendarsUseCase.getCalendars()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{
                        calendars = it
                        calendarsData.postValue(it)})
        }
        return calendarsData
    }

    fun getEvents(fromDay: DateTime, toDay: DateTime): LiveData<TreeMap<Long, MutableList<ScheduledEvent>>> {
        compositeDisposable.add(eventsUseCase.getEvents(fromDay, toDay)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnNext{eventsLiveData.postValue(it)}
                .subscribe())

        return eventsLiveData
    }

}