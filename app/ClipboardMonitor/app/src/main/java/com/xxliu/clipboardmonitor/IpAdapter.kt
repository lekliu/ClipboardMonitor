package com.xxliu.clipboardmonitor

import android.widget.Button
import android.widget.RadioButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class IpAdapter(
    private var ipList: List<String>,
    private var defaultIp: String?,
    private val listener: OnIpSelectedListener
) : RecyclerView.Adapter<IpAdapter.IpViewHolder>() {

    interface OnIpSelectedListener {
        fun onIpSelected(ip: String)
        fun onIpDeleted(ip: String)
    }

    inner class IpViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ip, parent, false)
        return IpViewHolder(view)
    }

    override fun onBindViewHolder(holder: IpViewHolder, position: Int) {
        val ip = ipList[position]
        val ipTextView = holder.view.findViewById<TextView>(R.id.ipTextView)
        val deleteButton = holder.view.findViewById<Button>(R.id.deleteIpButton)
        val selectButton = holder.view.findViewById<RadioButton>(R.id.selectIpRadioButton)

        ipTextView.text = ip
        selectButton.isChecked = ip == defaultIp

        selectButton.setOnClickListener {
            listener.onIpSelected(ip)
        }

        deleteButton.setOnClickListener {
            listener.onIpDeleted(ip)
        }
    }

    override fun getItemCount(): Int = ipList.size

    fun updateList(newIpList: List<String>) {
        this.ipList = newIpList
        notifyDataSetChanged()
    }

    fun updateDefault(newDefaultIp: String) {
        this.defaultIp = newDefaultIp
        notifyDataSetChanged()
    }
}
