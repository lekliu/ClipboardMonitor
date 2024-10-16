package com.xxliu.clipboardmonitor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xxliu.clipboardmonitor.models.ClipboardData
import com.xxliu.clipboardmonitor.repositories.ClipboardRepository
import com.xxliu.clipboardmonitor.utils.NetworkUtils
import com.xxliu.clipboardmonitor.viewmodels.ClipboardViewModel
import com.xxliu.clipboardmonitor.viewmodels.ClipboardViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var uploadButton: Button
    private val clipboardViewModel: ClipboardViewModel by viewModels {
        ClipboardViewModelFactory(ClipboardRepository())
    }
    private lateinit var adapter: ClipboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.clipboardRecyclerView)
        refreshButton = findViewById(R.id.refreshButton)
        uploadButton = findViewById(R.id.uploadButton)

        adapter = ClipboardAdapter(clipboardViewModel.clipboardDataList,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        refreshButton.setOnClickListener {
            fetchClipboardData()
        }

        uploadButton.setOnClickListener {
            uploadClipboardData()
        }
    }

    private fun fetchClipboardData() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            clipboardViewModel.fetchClipboardData { data ->
                if (data != null) {
                    clipboardViewModel.clipboardDataList.clear()
                    clipboardViewModel.clipboardDataList.addAll(data)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "获取数据时出错", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadClipboardData() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData? = clipboardManager.primaryClip

        if (clipData != null && clipData.itemCount > 0) {
            val newClipValue = clipData.getItemAt(0).text.toString()
            val newClipboardData = ClipboardData(key = "C${clipboardViewModel.clipboardDataList.size}", value = newClipValue)

            if (NetworkUtils.isNetworkAvailable(this)) {
                clipboardViewModel.uploadClipboardData(listOf(newClipboardData)) { success ->
                    if (success) {
                        clipboardViewModel.clipboardDataList.add(0, newClipboardData)
                        if (clipboardViewModel.clipboardDataList.size > 10) {
                            clipboardViewModel.clipboardDataList.removeAt(clipboardViewModel.clipboardDataList.size - 1)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "上传数据时出错", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "剪贴板为空", Toast.LENGTH_SHORT).show()
        }
    }
}
