package com.example.bugitcore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.usecase.EnqueueBugReportUC
import com.example.core.feature.bug_reporting.domain.usecase.GetBugByIdUC
import com.example.core.feature.bug_reporting.domain.usecase.ReportBugUC
import com.example.core_contracts.extensions.loge
import com.example.core_contracts.extensions.logw
import com.example.core_contracts.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface BugReportUiState {
    object Idle : BugReportUiState
    object Loading : BugReportUiState
    data class Success(val message: String) : BugReportUiState
    data class Error(val message: String) : BugReportUiState
}

@HiltViewModel
class BugReportViewModel @Inject constructor(
    private val reportBugUC: EnqueueBugReportUC,
    private val getBugByIdUC: GetBugByIdUC
) : ViewModel() {

    private val _uiState = MutableStateFlow<BugReportUiState>(BugReportUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun submitBug(description: String, imageUri: String) {
        val request = BugReportRequest(description = description, imageUriString = imageUri)

        reportBugUC.invoke(viewModelScope, request) { result ->
            _uiState.value = when (result) {
                is Resource.Progress -> if (result.loading) BugReportUiState.Loading else _uiState.value
                is Resource.Success -> {
                    observeBug(result.model)
                    BugReportUiState.Success("Bug Reported Successfully!")
                }
                is Resource.Failure -> BugReportUiState.Error(
                    result.exception.message ?: "Unknown Error"
                )
            }
        }
    }

    private fun observeBug(bug: Bug) {
        getBugByIdUC.invoke(viewModelScope, bug.id) { result ->
            when(result){
                is Resource.Failure -> "observeBug failed: ${result.exception}".loge()
                is Resource.Progress<*> -> {}
                is Resource.Success ->  "observeBug success: ${result.model}".logw()
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = BugReportUiState.Idle
    }
}