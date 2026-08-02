package com.ajantha.apsa.ui.applist

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajantha.apsa.model.AppUiModel
import com.ajantha.apsa.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppsViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppsUiState>(AppsUiState.Loading)
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        scanApps()
    }

    fun scanApps() {
        viewModelScope.launch {
            _uiState.value = AppsUiState.Loading
            try {
                val apps = repository.getApps()
                _uiState.value = AppsUiState.Success(apps)
            } catch (e: Exception) {
                _uiState.value = AppsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AppsUiState {
    data object Loading : AppsUiState()
    data class Success(val apps: List<AppUiModel>) : AppsUiState()
    data class Error(val message: String) : AppsUiState()
}

class AppsViewModelFactory(
    application: Application
) : ViewModelProvider.Factory {

    private val repository = AppRepository(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AppsViewModel::class.java)) {

            return AppsViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel")
    }
}