package com.ajantha.apsa.ui.appdetail

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

class AppDetailViewModel(application: Application) : ViewModel() {

    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow<AppDetailUiState>(AppDetailUiState.Loading)
    val uiState: StateFlow<AppDetailUiState> = _uiState.asStateFlow()

    fun load(packageName: String) {
        viewModelScope.launch {
            _uiState.value = AppDetailUiState.Loading
            try {
                val uiModel = repository.getAppByPackageName(packageName)
                if (uiModel == null) {
                    _uiState.value = AppDetailUiState.Error("App not found")
                } else {
                    _uiState.value = AppDetailUiState.Success(uiModel)
                }
            } catch (e: Exception) {
                _uiState.value = AppDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AppDetailUiState {
    data object Loading : AppDetailUiState()
    data class Success(val uiModel: AppUiModel) : AppDetailUiState()
    data class Error(val message: String) : AppDetailUiState()
}

class AppDetailViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppDetailViewModel(application) as T
    }
}
