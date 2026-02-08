package top.teanli.lightfalling.command.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import top.teanli.lightfalling.command.Command
import top.teanli.lightfalling.command.execute
import top.teanli.lightfalling.command.literal
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.MessageTool

object ListCommand : Command("list", "List all modules") {
    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.literal(name) {
            execute {
                val modules = ModuleManager.getModules().joinToString(", ") {
                    if (it.state) "${ChatFormatting.GREEN}${it.name}" else "${ChatFormatting.RED}${it.name}"
                }
                MessageTool.info("Modules: $modules")
                1
            }
        }
    }
}
