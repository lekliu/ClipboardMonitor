package com.xxliu.clipboardmonitor.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object PreferenceUtils {

    private const val PREFS_NAME = "clipboard_monitor_prefs"
    private const val KEY_IP_LIST = "ip_list"
    private const val KEY_DEFAULT_IP = "default_ip"

    /**
     * 获取所有保存的 IP 地址列表
     */
    fun getIpList(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ipJson = prefs.getString(KEY_IP_LIST, "[]") ?: "[]"
        val jsonArray = JSONArray(ipJson)
        val ipList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            ipList.add(jsonArray.getString(i))
        }
        return ipList
    }

    /**
     * 保存 IP 地址列表
     */
    private fun saveIpList(context: Context, ipList: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray(ipList)
        prefs.edit().putString(KEY_IP_LIST, jsonArray.toString()).apply()
    }

    /**
     * 获取默认 IP 地址
     */
    fun getDefaultIp(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEFAULT_IP, null)
    }

    /**
     * 设置默认 IP 地址
     */
    fun setDefaultIp(context: Context, ip: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_DEFAULT_IP, ip).apply()
    }

    /**
     * 添加新 IP 地址
     */
    fun addIp(context: Context, ip: String) {
        val ipList = getIpList(context).toMutableList()
        if (!ipList.contains(ip)) {
            ipList.add(ip)
            saveIpList(context, ipList)
        }
    }

    /**
     * 删除 IP 地址
     */
    fun removeIp(context: Context, ip: String) {
        val ipList = getIpList(context).toMutableList()
        if (ipList.contains(ip)) {
            ipList.remove(ip)
            saveIpList(context, ipList)
        }
    }
}
