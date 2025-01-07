package com.xxliu.clipboardmonitor.api

import com.xxliu.clipboardmonitor.models.ClipboardData
import com.xxliu.clipboardmonitor.utils.PreferenceUtils
import com.xxliu.clipboardmonitor.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection

import java.net.URL

object ApiService {
    // 动态设置 BASE_URL 的初始值为空
    private var baseUrl: String? = null

    /**
     * 设置当前使用的 BASE_URL（从选中或默认 IP 读取）
     */
    fun setBaseUrl(ipAddress: String) {
        baseUrl = "http://$ipAddress/"
    }

    /**
     * 获取完整的 BASE_URL，如果未设置则抛出异常
     */
    private fun getBaseUrl(): String {
        return baseUrl ?: throw IllegalStateException("BASE_URL 尚未设置，请调用 setBaseUrl() 设置 IP 地址")
    }

    // 通过协程在后台线程中执行 GET 请求
    suspend fun fetchClipboardData(): List<ClipboardData>? {
        return withContext(Dispatchers.IO) {
            val url = URL("${getBaseUrl()}api/clipdata")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            try {
                connection.inputStream.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val json = reader.readText()
                    val type = object : TypeToken<List<ClipboardData>>() {}.type
                    Gson().fromJson<List<ClipboardData>>(json, type)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                connection.disconnect()
            }
        }
    }

    // 通过协程在后台线程中执行 POST 请求
    suspend fun uploadClipboardData(data: List<ClipboardData>): Boolean {
        return withContext(Dispatchers.IO) {
            val url = URL("${getBaseUrl()}insert/clipdata")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            try {
                val outputStream: OutputStream = connection.outputStream
                val jsonData = Gson().toJson(data)
                outputStream.write(jsonData.toByteArray())
                outputStream.flush()
                outputStream.close()

                connection.responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                connection.disconnect()
            }
        }
    }
}
