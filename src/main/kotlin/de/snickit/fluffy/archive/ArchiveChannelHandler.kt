package de.snickit.fluffy.archive

import de.snickit.fluffy.Utils.addMembersToChannel
import de.snickit.fluffy.Utils.assignCategory
import de.snickit.fluffy.Utils.getCurrentSemester
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class ArchiveChannelHandler: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun archiveChannel(guildChannel: GuildChannel, channelMembers: List<Member>) {
        val prefix: String = getCurrentSemester() + "_"

        logger.info("Archiviere ${guildChannel.id} mit prefix $prefix und membern:")
        channelMembers.forEach {
            logger.info(it.effectiveName)
        }

        // TODO: User needs permissions
        // TODO: Delete role
        logger.info("Ändere den namen des channels ${guildChannel.name}")
        guildChannel.manager.setName(prefix + guildChannel.name).queue()
        assignCategory("archiv", guildChannel, true)
        addMembersToChannel(guildChannel, channelMembers)


        // TODO Post reaction, click to remove yourself from archive

    }

}
