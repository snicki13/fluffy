package de.snickit.fluffy.archive

import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

class ArchiveChannelHandler: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun archiveChannel(guildChannel: GuildChannel, channelMembers: List<Member>, suffix: String) {
        logger.info("Archiviere ${guildChannel.id} mit suffix $suffix und membern:")
        channelMembers.forEach {
            logger.info(it.effectiveName)
        }
        assignCategory(guildChannel)
    }

    private fun assignCategory(guildChannel: GuildChannel) {
        val guild = guildChannel.guild
        val cats = guild.getCategoriesByName("archiv", true)
        if (cats.size != 1) {
            throw Exception("Category archiv does not exist or is ambivalent!")
        }
        guildChannel.manager.setParent(cats.first()).queue()
    }

}
