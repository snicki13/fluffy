package de.snickit.fluffy.createModule

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class CreateModuleHandler: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createModule(event: MessageReceivedEvent, commandTokens: List<String>) {


    }
}