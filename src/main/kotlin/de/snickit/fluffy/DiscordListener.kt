package de.snickit.fluffy

import de.snickit.fluffy.message.MorningMessageResponder
import de.snickit.fluffy.message.NightMessageResponder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZoneId

class DiscordListener: ListenerAdapter(), KoinComponent {

    private val morningMessageResponder by inject<MorningMessageResponder>()
    private val nightMessageResponder by inject<NightMessageResponder>()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot) return
        val messageTimestamp = event.message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        val channel = event.channel
        when (messageTimestamp.hour) {
            in  0..4  -> nightMessageResponder.respondToMessage(event.channel, event.message)
            in  5..10 -> morningMessageResponder.respondToMessage(event.channel, event.message)
        }
    }

    override fun onUserTyping(event: UserTypingEvent) {
    }



}