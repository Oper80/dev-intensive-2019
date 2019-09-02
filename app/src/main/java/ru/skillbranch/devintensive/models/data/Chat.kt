package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
        val id: String,
        val title: String,
        val members: List<User> = listOf(),
        var messages: MutableList<BaseMessage> = mutableListOf(),
        var isArchived: Boolean = false
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int {

        return messages.filter { !it.isReaded }.size
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date? {
        return when(val lastMessage = messages.lastOrNull()){
            null -> null
            else -> lastMessage.date
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String?, String?> = when (val lastMessage = messages.lastOrNull()) {
        is TextMessage -> lastMessage.text to lastMessage.from.firstName
        is ImageMessage -> "${lastMessage.from.firstName} - отправил фото" to lastMessage.from.firstName
        else -> null to null
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                    id,
                    user.avatar,
                    Utils.toInitials(user.firstName, user.lastName) ?: "??",
                    "${user.firstName ?: ""} ${user.lastName ?: ""}",
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    user.isOnline,
                    ChatType.SINGLE
            )
        } else {
            ChatItem(
                    id,
                    null,
                    "",
                    title,
                    lastMessageShort().first,
                    unreadableMessageCount(),
                    lastMessageDate()?.shortFormat(),
                    false,
                    ChatType.GROUP,
                    lastMessageShort().second
            )
        }
    }

    fun toArchiveChatItem(chats : List<Chat>): ChatItem {
        var unreadMessages = 0
        for(c in chats){
            unreadMessages += c.messages.filter { !it.isReaded }.size
        }
        return ChatItem(
                "0",
                null,
                "",
                "Архив чатов",
                lastMessageShort().first,
                unreadMessages,
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.ARCHIVE,
                lastMessageShort().second
        )
    }
}

enum class ChatType {
    ARCHIVE,
    SINGLE,
    GROUP
}



