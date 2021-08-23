package de.snickit.fluffy

import de.snickit.fluffy.archive.ArchiveChannelHandler
import de.snickit.fluffy.message.MorningMessageResponder
import de.snickit.fluffy.message.NightMessageResponder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZoneId

class DiscordListener: ListenerAdapter(), KoinComponent {

    private val morningMessageResponder by inject<MorningMessageResponder>()
    private val nightMessageResponder by inject<NightMessageResponder>()
    private val archiveChannelHandler by inject<ArchiveChannelHandler>()

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        val messageContent = event.message.contentRaw
        val matches = Regex("/archive\\s+([a-zA-Z0-9_-]+)").matchEntire(messageContent)
        if (event.isFromGuild && matches != null) {
            archiveChannel(event, matches.groupValues[1])
        }
        if (event.author.isBot) return
        val messageTimestamp = event.message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        when (messageTimestamp.hour) {
            in  0..4  -> nightMessageResponder.respondToMessage(event.channel, event.message)
            in  5..10 -> morningMessageResponder.respondToMessage(event.channel, event.message)
        }
    }

    private fun archiveChannel(event: MessageReceivedEvent, suffix: String) {
        val guild = event.guild
        val guildChannel = guild.getGuildChannelById(event.channel.id)
        guild.loadMembers().onSuccess { guildMembers ->
            val channelMembers = guildMembers
                .filter { member ->
                    member.hasPermission(guildChannel!!, Permission.VIEW_CHANNEL) &&
                            !member.user.isBot
                }
            archiveChannelHandler.archiveChannel(guildChannel!!, channelMembers, suffix)
        }
    }



}
