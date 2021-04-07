package com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.CurrentRaceDistance
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.exception.CheckpointNotFoundException
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.exception.SyncWithServerException
import com.gmail.maystruks08.domain.interactors.CheckpointInteractor
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.base.SingleLiveEvent
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toCheckpointView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class SelectCheckpointDialogViewModel @ViewModelInject constructor(
    private val checkpointInteractor: CheckpointInteractor,
) : BaseViewModel() {

    val showCheckpoints get() = _showCheckpointsStateFlow
    val showProgress get() = _showProgressLiveData


    private val _showCheckpointsStateFlow = MutableStateFlow<ArrayList<CheckpointView>>(ArrayList())
    private val _showProgressLiveData = SingleLiveEvent<Boolean>()


    fun init(raceDistance: CurrentRaceDistance) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result =
                checkpointInteractor.getCheckpoints(raceDistance.first, raceDistance.second)) {
                is TaskResult.Value -> {
                    val currentCheckpoint = (checkpointInteractor.getCurrentSelectedCheckpoint(
                        raceDistance.first,
                        raceDistance.second
                    ) as? TaskResult.Value)?.value

                    val checkpoints =
                        ArrayList(result.value.map { it.toCheckpointView(currentCheckpoint?.getId()) })
                    _showCheckpointsStateFlow.value = checkpoints
                }
                is TaskResult.Error -> handleError(result.error)
            }
        }
    }

    private fun handleError(e: Throwable) {
        _showProgressLiveData.postValue(false)
        when (e) {
            is SaveRunnerDataException -> {
                Timber.e(e)
                toastLiveData.postValue("Не удалось сохранить данные участника:" + e.message)
            }
            is RunnerNotFoundException -> {
                Timber.e(e)
                toastLiveData.postValue("Участник не найден")
            }
            is SyncWithServerException -> {
                Timber.e(e)
                toastLiveData.postValue("Данные не сохранились на сервер")
            }
            is CheckpointNotFoundException -> toastLiveData.postValue("КП не выбрано для дистанции")
            else -> Timber.e(e)
        }
    }
}