package de.snickit.fluffy

import de.snickit.fluffy.Utils.checkChannelAndRolesPermission
import de.snickit.fluffy.archive.ArchiveChannelHandler
import de.snickit.fluffy.createModule.CreateModuleHandler
import de.snickit.fluffy.message.MessageResponder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.time.ZoneId

class DiscordListener : ListenerAdapter(), KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val messageResponder by inject<MessageResponder>()
    private val archiveChannelHandler by inject<ArchiveChannelHandler>()
    private val createModuleHandler by inject<CreateModuleHandler>()

    private val commandTokenRegex = Regex("^(/\\S+)(?:\\s+(?:([^\"]\\S+)|\"(.+)\"))*\$")

    override fun onMessageReceived(event: MessageReceivedEvent) {
        super.onMessageReceived(event)
        if (event.author.isBot || !event.isFromGuild) return
        val commandTokens = commandTokenRegex.matchEntire(event.message.contentStripped)
        ?.groupValues
            ?.drop(1) // drop full match
            ?.filter { it != "" } // group values keeps non-matched groups as empty string or null

        when (commandTokens?.first()) {
            "/archive" -> archiveChannel(event)
            "/create" ->
                createModule(event, commandTokens.drop(1)) // remove command token itself
            else -> messageResponder(event)
        }
    }

    private fun messageResponder(event: MessageReceivedEvent){

        if(event.textChannel.parent?.name == "Module")
            return

        val messageTimestamp = event.message.timeCreated.atZoneSameInstant(ZoneId.of("Europe/Berlin"))
        when (messageTimestamp.hour) {
            in 0..4 -> messageResponder.nightlyResponse(event.channel, event.message)
            in 5..10 -> messageResponder.dailyResponse(event.channel, event.message)
        }
    }

    /**
     * Calls the create-Module function, checking permissions beforehand.
     * Creates a new text-channel, using the given command tokens.
     * Also creates an associated role and sets up permissions, including a self-assign message within the #rollen-module channel.
     * @param event The message event which includes the create command
     * @param commandTokens List of arguments, syntax: `/create module-name [full module name] [role-Color] [role-select-emoji]`
     */
    private fun createModule(event: MessageReceivedEvent, commandTokens: List<String>) {
        if(!checkChannelAndRolesPermission(event))
            return

        logger.info("Permissions check passed")

        createModuleHandler.createModule(event, commandTokens)
        return
    }


    private fun archiveChannel(event: MessageReceivedEvent) {
        if(!checkChannelAndRolesPermission(event))
            return

        logger.info("Permissions check passed")

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
        return
    }


}
