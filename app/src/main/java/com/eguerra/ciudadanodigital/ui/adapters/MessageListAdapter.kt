package com.eguerra.ciudadanodigital.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eguerra.ciudadanodigital.R
import com.eguerra.ciudadanodigital.data.local.entity.MessageModel

class MessageListAdapter(
    private var dataSet: MutableList<MessageModel>, private val operationListener: MessageListener
) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    interface MessageListener {
        fun onReferencesRequested(message: MessageModel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val infoButton: ImageButton? = view.findViewById(R.id.itemMessageReceived_infoButton)
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].source == "user") 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout =
            if (viewType == 1) R.layout.item_message_sent else R.layout.item_message_received
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = dataSet[position]
        holder.messageText.text = message.content

        if (!message.reference.isNullOrBlank() && holder.infoButton != null) {
            holder.infoButton.visibility = View.VISIBLE
            holder.infoButton.setOnClickListener {
                operationListener.onReferencesRequested(message)
            }
        } else {
            holder.infoButton?.visibility = View.GONE
        }
    }

    override fun getItemCount() = dataSet.size

    fun addMessage(message: MessageModel) {
        dataSet.add(message)
        notifyItemInserted(dataSet.size - 1)
    }

    fun setMessages(messages: List<MessageModel>) {
        dataSet.clear()
        dataSet.addAll(messages)
        notifyDataSetChanged()
    }
}
