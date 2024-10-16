package com.xxliu.clipboardmonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xxliu.clipboardmonitor.models.ClipboardData
import android.content.ClipboardManager
import android.content.Context
import android.content.ClipData
import android.widget.Toast

class ClipboardAdapter(private val clipboardDataList: MutableList<ClipboardData>, private val context: Context) :
    RecyclerView.Adapter<ClipboardAdapter.ClipboardViewHolder>() {

    inner class ClipboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keyTextView: TextView = itemView.findViewById(R.id.keyTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)

        init {
            itemView.setOnClickListener {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Value", valueTextView.text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context,"内容已拷入剪贴板", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(clipboardData: ClipboardData) {
            keyTextView.text = clipboardData.key
            valueTextView.text = clipboardData.value
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClipboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_clipboard, parent, false)
        return ClipboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClipboardViewHolder, position: Int) {
        holder.bind(clipboardDataList[position])
    }

    override fun getItemCount(): Int {
        return clipboardDataList.size
    }
}
