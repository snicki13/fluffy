package de.snickit.fluffy

import de.snickit.fluffy.archive.ArchiveChannelHandler
import de.snickit.fluffy.createModule.CreateModuleHandler
import de.snickit.fluffy.message.MorningMessageResponder
import de.snickit.fluffy.message.NightMessageResponder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZoneId

class DiscordListener : ListenerAdapter(), KoinComponent {

    private val morningMessageResponder by inject<MorningMessageResponder>()
    private val nightMessageResponder by inject<NightMessageResponder>()
    private val archiveChannelHandler by inject<ArchiveChannelHandler>()
    private val createModuleHandler by inject<CreateModuleHandler>()

    private val commandTokenRegex = Regex("^(/\\S+)(?:\\s+(?:([^\"]\\S*)|\"(.*)\"))*\$")

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot || !event.isFromGuild) return
        val commandTokens = commandTokenRegex.matchEntire(event.message.contentStripped)?.groupValues

        when (commandTokens?.get(1)) {
            "/archive" -> archiveChannel(event)
            "/create" ->
                createModule(event, commandTokens)
            else -> nonKeywordCommand(event)
        }
    }

    private fun nonKeywordCommand(event: MessageReceivedEvent){
        val messageTimestamp = event.message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        when (messageTimestamp.hour) {
            in 0..4 -> nightMessageResponder.respondToMessage(event.channel, event.message)
            in 5..10 -> morningMessageResponder.respondToMessage(event.channel, event.message)
        }
    }

    private fun createModule(event: MessageReceivedEvent, commandTokens: List<String>) {

        /*
        Syntax:
        /create module-name [Full Name] [Color] [emoji]
         */

        createModuleHandler.createModule(event, commandTokens)
    }

    private fun archiveChannel(event: MessageReceivedEvent) {
        val guild = event.guild
        val guildChannel = guild.getGuildChannelById(event.channel.id)!!

        if (!guild.getCategoriesByName("MODULE", true).contains(guildChannel.parent)) {
            event.channel.sendMessage("YOU SHALL NOT PASS (or archive) this Channel!").queue()
            return
        }

        // Get list of members
        guild.loadMembers().onSuccess { guildMembers ->
            val channelMembers = guildMembers.filter { member ->
                    member.hasPermission(guildChannel, Permission.VIEW_CHANNEL) &&
                            !member.user.isBot &&
                            !member.hasPermission(Permission.ADMINISTRATOR)
                }
            archiveChannelHandler.archiveChannel(event, guildChannel, channelMembers)
        }

        event.message.delete().queue()
    }


}
