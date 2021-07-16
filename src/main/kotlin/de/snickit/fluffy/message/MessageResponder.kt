package de.snickit.fluffy.message

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel

interface MessageResponder {

    fun respondToMessage(channel: MessageChannel, message: Message)

}