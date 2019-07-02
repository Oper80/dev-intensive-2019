package ru.skillbranch.devintensive.models

import ru.skillbranch.devintensive.extensions.humanizeDiff
import java.util.*

class TextMessage(
    id: String,
    from: User?,
    chat: Chat,
    date: Date = Date(),
    var text: String?,
    isIncoming: Boolean = false
) : BaseMessage(id, from, chat, date, isIncoming) {

    override fun formatMessage(): String = "${from?.firstName} " +
    "${if(isIncoming) "получил" else "отправил" } сообщение \"$text\" ${date.humanizeDiff()}"
}