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

    private fun isArchiveCommand(messageContent: String): Boolean =
        Regex("/archive\\s*").matchEntire(messageContent) != null

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot) return

        val messageContent = event.message.contentRaw
        if (event.isFromGuild && isArchiveCommand(messageContent)) {
            // TODO: Channel must be in Module category, error otherwise
            //archiveChannel(event) TODO uncomment when ready
        }
        val messageTimestamp = event.message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        when (messageTimestamp.hour) {
            in  0..4  -> nightMessageResponder.respondToMessage(event.channel, event.message)
            in  5..10 -> morningMessageResponder.respondToMessage(event.channel, event.message)
        }
    }

    private fun archiveChannel(event: MessageReceivedEvent) {
        val guild = event.guild
        val guildChannel = guild.getGuildChannelById(event.channel.id)
        guild.loadMembers().onSuccess { guildMembers ->
            val channelMembers = guildMembers
                .filter { member ->
                    member.hasPermission(guildChannel!!, Permission.VIEW_CHANNEL) &&
                            !member.user.isBot
                }
            archiveChannelHandler.archiveChannel(guildChannel!!, channelMembers)
        }
    }



}
