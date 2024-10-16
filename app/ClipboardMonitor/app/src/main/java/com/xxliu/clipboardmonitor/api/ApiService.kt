package com.xxliu.clipboardmonitor.api

import com.xxliu.clipboardmonitor.models.ClipboardData
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
    private const val BASE_URL = "http://lek.tpddns.cn:3388/"

    // 通过协程在后台线程中执行 GET 请求
    suspend fun fetchClipboardData(): List<ClipboardData>? {
        return withContext(Dispatchers.IO) {
            val url = URL("${BASE_URL}api/clipdata")
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
            val url = URL("${BASE_URL}insert/clipdata")
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
