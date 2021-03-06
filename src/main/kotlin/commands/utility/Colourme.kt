package commands.utility

import commandhandler.CommandContext
import commands.BaseCommand
import commands.CommandTypes.Utility
import database.colourmeRoles
import ext.useArguments
import net.dv8tion.jda.api.exceptions.ErrorHandler
import net.dv8tion.jda.api.requests.ErrorResponse
import java.awt.Color

class Colourme : BaseCommand(
    commandName = "colourme",
    commandDescription = "Create a custom role for yourself",
    commandType = Utility,
    commandArguments = listOf("<color> <role name>"),
    commandAliases = listOf("colorme")
) {

    override fun execute(ctx: CommandContext) {
        super.execute(ctx)
        val member = ctx.authorAsMember
        val guildId = ctx.guild.id
        if (member!!.roles.none { guildId.colourmeRoles.contains(it.id) }) {
            channel.sendMessage("You are not allowed to use this command!").queueAddReaction()
            return
        }
        val args = ctx.args
        if (args.isNotEmpty() && args.size >= 2) {
            val color = try {
                Color.decode(args[0])
            } catch (e: Exception) {
                Color.BLACK
            }

            val roleName = args.apply { removeAt(0) }.joinToString(" ")
            val ccrole = ctx.authorAsMember?.roles?.filter { it.name.endsWith("CC") }
            if (ccrole != null) {
                fun addRole() {
                    ctx.guild.createRole().setColor(color).setName("$roleName-CC").queue({ role ->
                        ctx.guild.modifyRolePositions().selectPosition(role).moveTo(member.roles.first().position + 1).queue {
                            ctx.guild.addRoleToMember(member, role).queue {
                                channel.sendMessage("Successfully added the role!").queueAddReaction()
                            }
                        }
                    }, ErrorHandler().handle(ErrorResponse.MAX_ROLES_PER_GUILD) {
                        channel.sendMessage("Guild reached maximum amount of roles!").queueAddReaction()
                    })
                }

                if (ccrole.isNotEmpty()) {
                    ctx.guild.removeRoleFromMember(ctx.authorAsMember!!, ccrole[0]).queue {
                        addRole()
                        return@queue
                    }
                }
                addRole()
            }

        } else {
            useArguments(2)
        }
    }

}