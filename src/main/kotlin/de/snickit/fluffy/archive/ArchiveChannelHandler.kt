package de.snickit.fluffy.archive

import de.snickit.fluffy.Utils.addMembersToChannel
import de.snickit.fluffy.Utils.assignCategory
import de.snickit.fluffy.Utils.getCurrentSemester
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class ArchiveChannelHandler: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun archiveChannel(event: MessageReceivedEvent, guildChannel: GuildChannel, channelMembers: List<Member>) {
        val prefix: String = getCurrentSemester() + "_"

        logger.info("Archiviere ${guildChannel.id} mit prefix $prefix und membern:")
        channelMembers.forEach {
            logger.info(it.effectiveName)
        }

        // TODO: User needs permissions
        // TODO: Delete role
        logger.info("Ändere den namen des channels ${guildChannel.name}")
        guildChannel.manager.setName(prefix + guildChannel.name)
        assignCategory("archiv", guildChannel, true)
        addMembersToChannel(guildChannel, channelMembers)

        val roles = guildChannel.guild.getRolesByName(guildChannel.name, true)
        if(roles.size > 1){
            logger.info("More than one role was found: $roles")
            event.channel.sendMessage("Archive: More than one role was found, please delete manually")
        }
        else{
            when (val moduleRole = roles.firstOrNull()) {
                null -> {
                    logger.info("No role associated with this channel was found. Delete manually")
                    event.channel.sendMessage("Archive: No role associated with this channel was found. Delete manually.")
                }
                else -> {
                    logger.info("deleted role $moduleRole")
                    moduleRole.delete().queue()
                }
            }
        }
        guildChannel.manager.complete()


        // TODO Post reaction, click to remove yourself from archive

    }

}
