package com.xxliu.clipboardmonitor

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xxliu.clipboardmonitor.api.ApiService
import com.xxliu.clipboardmonitor.models.ClipboardData
import com.xxliu.clipboardmonitor.repositories.ClipboardRepository
import com.xxliu.clipboardmonitor.utils.NetworkUtils
import com.xxliu.clipboardmonitor.utils.PreferenceUtils
import com.xxliu.clipboardmonitor.viewmodels.ClipboardViewModel
import com.xxliu.clipboardmonitor.viewmodels.ClipboardViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshButton: Button
    private lateinit var uploadButton: Button
    private lateinit var configureButton: Button
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
        configureButton = findViewById(R.id.configureButton)

        adapter = ClipboardAdapter(clipboardViewModel.clipboardDataList, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        refreshButton.setOnClickListener {
            fetchClipboardData()
        }

        uploadButton.setOnClickListener {
            uploadClipboardData()
        }

        configureButton.setOnClickListener {
            configureServerIp()
        }
    }

    private fun fetchClipboardData() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            PreferenceUtils.getDefaultIp(this@MainActivity)?.let { ApiService.setBaseUrl(it) }
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
                PreferenceUtils.getDefaultIp(this@MainActivity)?.let { ApiService.setBaseUrl(it) }
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

    private lateinit var adapterIp: IpAdapter

    private fun configureServerIp() {
        val ipList = PreferenceUtils.getIpList(this)
        val defaultIp = PreferenceUtils.getDefaultIp(this)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("配置服务器 IP 地址")

        // 使用自定义布局
        val dialogView = layoutInflater.inflate(R.layout.dialog_ip_config, null)
        val ipRecyclerView = dialogView.findViewById<RecyclerView>(R.id.ipRecyclerView)
        val addButton = dialogView.findViewById<Button>(R.id.addIpButton)
        val inputField = dialogView.findViewById<EditText>(R.id.newIpEditText)

        adapterIp = IpAdapter(ipList, defaultIp, object : IpAdapter.OnIpSelectedListener {
            override fun onIpSelected(ip: String) {
                PreferenceUtils.setDefaultIp(this@MainActivity, ip)
                Toast.makeText(this@MainActivity, "默认 IP 设置为: $ip", Toast.LENGTH_SHORT).show()
                adapterIp.updateDefault(ip)
            }

            override fun onIpDeleted(ip: String) {
                PreferenceUtils.removeIp(this@MainActivity, ip)
                adapterIp.updateList(PreferenceUtils.getIpList(this@MainActivity))
            }
        })

        ipRecyclerView.layoutManager = LinearLayoutManager(this)
        ipRecyclerView.adapter = adapterIp

        // 添加新 IP
        addButton.setOnClickListener {
            val newIp = inputField.text.toString()
            if (newIp.isNotBlank()) {
                PreferenceUtils.addIp(this, newIp)
                adapterIp.updateList(PreferenceUtils.getIpList(this))
                inputField.text.clear()
            } else {
                Toast.makeText(this, "请输入有效的 IP 地址", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(dialogView)
        builder.setPositiveButton("关闭", null)
        builder.show()
    }

}
