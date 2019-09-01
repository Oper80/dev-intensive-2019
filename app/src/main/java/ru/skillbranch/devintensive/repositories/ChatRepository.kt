package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.utils.DataGenerator
import kotlin.math.max


object ChatRepository {
    private  val chats = CacheManager.loadChats()

    fun loadChats() : MutableLiveData<List<Chat>>{
        return chats
    }

    fun find(chatId: String): Chat? {
        val ind = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(ind)
    }

    fun update(chat : Chat) {

        val copy = chats.value!!.toMutableList()
        val ind = chats.value!!.indexOfFirst { it.id == chat.id }
        val lastArchiveChat = getLastArchiveChat()?.copy(id = CacheManager.nextChatId(), isArchived = false, isLastArchiveChat = true)
        val archiveChat = chats.value!!.firstOrNull { it.isLastArchiveChat }
        if(ind == -1) return
        copy[ind] = chat

        if(archiveChat == null) {
            if(lastArchiveChat != null) {
                copy.add(0, lastArchiveChat)
            }
        }else{
            if(lastArchiveChat != null) {
                copy[archiveChat.id.toInt()] = lastArchiveChat
            }
        }
        chats.value = copy
    }

    fun getLastArchiveChat(): Chat? {
        val archiveChats = chats.value!!.filter{it.isArchived}
        val f = Comparator<BaseMessage> { m1, m2 ->
            when{
                m1.date > m2.date -> 1
                else -> -1
            }
        }
        val lastArchiveChat : Chat? = archiveChats.maxWith(Comparator { c1, c2 ->
            when{
                c1?.messages?.maxWith(f)?.date!! > c2?.messages?.maxWith(f)?.date -> 1
                else -> -1
            }
        })
        return lastArchiveChat
    }
}