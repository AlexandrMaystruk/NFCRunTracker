package com.gmail.maystruks08.nfcruntracker.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.domain.entities.ResultOfTask
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerNotFoundException
import com.gmail.maystruks08.domain.exception.SaveRunnerDataException
import com.gmail.maystruks08.domain.interactors.RunnersInteractor
import com.gmail.maystruks08.domain.isolateSpecialSymbolsForRegex
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerResultView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerResultView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class RunnerResultViewModel @Inject constructor(
    private val router: Router,
    private val interactor: RunnersInteractor
) : BaseViewModel() {

    val runnerResults get(): LiveData<List<RunnerResultView>> = _runnerResultsLiveData
    val error get() : LiveData<Throwable> = _errorLiveData

    private val _runnerResultsLiveData = MutableLiveData<List<RunnerResultView>>()
    private val _errorLiveData = MutableLiveData<Throwable>()

    private var type: RunnerType = RunnerType.NORMAL

    fun provideFinishers(type: RunnerType){
        this.type = type
        viewModelScope.launch(Dispatchers.IO) {
            when (val onResult = interactor.getFinishers(type)) {
                is ResultOfTask.Value -> {
                    val sortedResultList = onResult.value
                        .mapIndexed { index: Int, runner: Runner -> runner.toRunnerResultView(index + 1) }
                    _runnerResultsLiveData.postValue(sortedResultList)
                }
                is ResultOfTask.Error -> handleError(onResult.error)
            }
        }
    }

    private fun handleError(e: Exception) {
        when (e) {
            is RunnerNotFoundException, is SaveRunnerDataException -> _errorLiveData.postValue(e)
        }
    }

    fun onBackClicked() {
        router.exit()
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.isNotEmpty()) {
                when (val result = interactor.getFinishers(type)) {
                    is ResultOfTask.Value -> {
                        val pattern = ".*${query.isolateSpecialSymbolsForRegex().toLowerCase()}.*".toRegex()
                        val filteredList = result.value
                            .filter { pattern.containsMatchIn(it.number.toString().toLowerCase()) }
                            .mapIndexed { index: Int, runner: Runner ->
                                runner.toRunnerResultView(index + 1)
                            }
                        _runnerResultsLiveData.postValue(filteredList)
                    }
                    is ResultOfTask.Error -> handleError(result.error)
                }
            } else provideFinishers(type)
        }
    }
}