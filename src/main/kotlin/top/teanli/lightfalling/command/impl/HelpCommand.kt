package top.teanli.lightfalling.command.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import top.teanli.lightfalling.command.Command
import top.teanli.lightfalling.command.CommandSystem
import top.teanli.lightfalling.command.execute
import top.teanli.lightfalling.command.literal
import top.teanli.lightfalling.tool.MessageTool

class HelpCommand : Command("help", "Displays the usage and description of all commands") {
    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.literal(name) {
            execute {
                MessageTool.info("${ChatFormatting.GOLD}--- LightFalling Commands ---")
                CommandSystem.getCommands().forEach { cmd ->
                    MessageTool.info("${ChatFormatting.BLUE}/lf ${cmd.name}${ChatFormatting.GRAY} - ${cmd.description}")
                }
                1
            }
        }
    }
}
