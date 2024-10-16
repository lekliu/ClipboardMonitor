package com.xxliu.clipboardmonitor.repositories

import com.xxliu.clipboardmonitor.api.ApiService
import com.xxliu.clipboardmonitor.models.ClipboardData

class ClipboardRepository {
    suspend fun fetchClipboardData(): List<ClipboardData>? {
        return ApiService.fetchClipboardData()
    }

    suspend fun uploadClipboardData(data: List<ClipboardData>): Boolean {
        return ApiService.uploadClipboardData(data)
    }
}
