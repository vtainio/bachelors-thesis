/*
* Copyright 2016 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.villetainio.familiarstrangers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.villetainio.familiarstrangers.models.ChatMessage
import com.villetainio.familiarstrangers.R
import java.util.*

class ChatAdapter(context: Context, resourceId: Int) : ArrayAdapter<ChatMessage>(context, resourceId) {
    val chatMessages = ArrayList<ChatMessage>()
    val mContext = context

    override fun add(message: ChatMessage) {
        chatMessages.add(message)
        super.add(message)
    }

    override fun getCount() : Int {
        return chatMessages.size
    }

    override fun getItem(index: Int): ChatMessage {
        return chatMessages.get(index)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val chatMessage = getItem(position)
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var row: View
        if (chatMessage.left) {
            row = inflater.inflate(R.layout.item_chat_left, parent, false)
        } else {
            row = inflater.inflate(R.layout.item_chat_right, parent, false)
        }
        val chatText = row.findViewById(R.id.chatBubbleMessage) as TextView
        chatText.text = chatMessage.message
        return row
    }
}
