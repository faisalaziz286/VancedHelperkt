package ext

import database.errorChannel
import database.modlogChannel
import database.owners
import jda
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

fun EmbedBuilder.sendStacktrace(guild: Guild, title: String?, stacktrace: String?) {
    val errorChannel = guild.id.errorChannel
    if (errorChannel.isEmpty()) {
        return
    }
    guild.getTextChannelById(errorChannel)?.sendMessage(
        MessageBuilder().setEmbed(
            apply {
                setTitle(title)
                setDescription(stacktrace?.take(2045) + "...")
            }.build()
        ).setContent(guild.id.owners.joinToString(" ") { it.asMention }).build()
    )?.queue()
}

fun EmbedBuilder.sendModLog(title: String, user: Member, mod: Member, reason: String?, guildId: String) {
    jda?.getTextChannelById(guildId.modlogChannel)?.sendMessage(
        apply {
            setTitle(title)
            addField(
                "User",
                user.user.getModLogInfo(),
                false
            )
            addField(
                "Moderator",
                mod.user.getModLogInfo(),
                false
            )
            if (reason != null) {
                addField(
                    "Reason",
                    reason,
                    false
                )
            }
        }.build()
    )?.queue()
}

fun EmbedBuilder.sendModLog(title: String, userId: String, mod: Member, reason: String?, guildId: String) {
    jda?.getTextChannelById(guildId.modlogChannel)?.sendMessage(
        apply {
            setTitle(title)
            addField(
                "User",
                userId,
                false
            )
            addField(
                "Moderator",
                mod.user.getModLogInfo(),
                false
            )
            if (reason != null) {
                addField(
                    "Reason",
                    reason,
                    false
                )
            }
        }.build()
    )?.queue()
}

fun EmbedBuilder.sendWarnLog(user: Member, mod: Member, reason: String?, guildId: String) {
    sendModLog("User Warned", user, mod, reason, guildId)
}

fun EmbedBuilder.sendMuteLog(user: Member, mod: Member, reason: String?, guildId: String) {
    sendModLog("User Muted", user, mod, reason, guildId)
}

fun EmbedBuilder.sendBanLog(user: Member, mod: Member, reason: String?, guildId: String) {
    sendModLog("User Banned", user, mod, reason, guildId)
}

fun EmbedBuilder.sendUnwarnLog(user: Member, mod: Member, guildId: String) {
    sendModLog("User Unwarned", user, mod, null, guildId)
}

fun EmbedBuilder.sendUnmuteLog(user: Member, mod: Member, guildId: String) {
    sendModLog("User Unmuted", user, mod, null, guildId)
}