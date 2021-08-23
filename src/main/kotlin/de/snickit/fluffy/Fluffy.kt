package de.snickit.fluffy

import de.snickit.fluffy.archive.ArchiveChannelHandler
import de.snickit.fluffy.message.MorningMessageResponder
import de.snickit.fluffy.message.NightMessageResponder
import io.github.serpro69.kfaker.Faker
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.slf4j.LoggerFactory

object Fluffy: KoinComponent {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Fluffy Version: 0.1")
        val publicKey = System.getenv("FLUFFY_DISCORD_TOKEN")

        initDependencyInjection()

        val discordListener by inject<DiscordListener>()
        val bot = JDABuilder
            .createDefault(publicKey)
            .enableIntents(GatewayIntent.GUILD_MEMBERS)
            .addEventListeners(discordListener)
            .build()
        bot.awaitReady()
    }

    private fun initDependencyInjection() {
        startKoin {
            printLogger(Level.INFO)
            modules(
                module {
                    single { Faker() }
                    single { MorningMessageResponder() }
                    single { NightMessageResponder() }
                    single { DiscordListener() }
                    single { ArchiveChannelHandler() }
                }
            )
        }
    }

}

