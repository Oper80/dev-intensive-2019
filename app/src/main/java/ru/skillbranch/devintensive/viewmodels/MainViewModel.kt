package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val chatRepository = ChatRepository
    private var query = mutableLiveData("")

    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->

        return@map chats.groupBy { it.isArchived }
                .flatMap { (isArchived, chats) ->
                    if (isArchived) listOf(toArchiveItem(chats))
                    else chats.map { it.toChatItem() }

                }.sortedWith(compareBy({it.chatType}, {it.id.toInt()}))
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()
        val filterF = {
            val queryStr = query.value!!
            val chatsList = chats.value!!
            result.value = if (queryStr.isEmpty()) chatsList
            else chatsList.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(chats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }
        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }

    private fun toArchiveItem(chats: List<Chat>): ChatItem {

        val f = Comparator<BaseMessage?> { m1, m2 ->
            when {
                m2 == null -> 1
                m1 == null -> -1
                m1.date > m2.date -> 1
                else -> -1
            }
        }
        var lastArchiveChat: Chat? = chats.maxWith(Comparator { c1, c2 ->
            when {
                c2.messages.size == 0 -> 1
                c1.messages.size == 0 -> -1
                c1?.messages?.maxWith(f)?.date!! > c2?.messages?.maxWith(f)?.date -> 1
                else -> -1
            }
        })
        if (lastArchiveChat == null) {
            lastArchiveChat = chats[0]
        }
        return lastArchiveChat.toArchiveChatItem(chats)
    }
}


