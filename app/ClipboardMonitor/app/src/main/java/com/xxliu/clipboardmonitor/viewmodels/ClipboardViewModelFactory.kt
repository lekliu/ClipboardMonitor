package com.xxliu.clipboardmonitor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xxliu.clipboardmonitor.repositories.ClipboardRepository

class ClipboardViewModelFactory(private val repository: ClipboardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClipboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
