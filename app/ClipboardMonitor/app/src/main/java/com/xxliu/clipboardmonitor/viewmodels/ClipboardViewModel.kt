package com.xxliu.clipboardmonitor.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxliu.clipboardmonitor.models.ClipboardData
import com.xxliu.clipboardmonitor.repositories.ClipboardRepository
import kotlinx.coroutines.launch

class ClipboardViewModel(private val repository: ClipboardRepository) : ViewModel() {
    val clipboardDataList = mutableListOf<ClipboardData>()

    fun fetchClipboardData(callback: (List<ClipboardData>?) -> Unit) {
        viewModelScope.launch {
            // 直接从 repository 中获取数据
            val data = repository.fetchClipboardData()
            callback(data) // 调用回调函数并传入获取到的数据
        }
    }

    fun uploadClipboardData(data: List<ClipboardData>, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.uploadClipboardData(data)
            callback(success) // 调用回调函数并传入成功状态
        }
    }
}



