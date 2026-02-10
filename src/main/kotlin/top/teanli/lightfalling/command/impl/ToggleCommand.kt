package top.teanli.lightfalling.command.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import top.teanli.lightfalling.command.*
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.MessageTool

class ToggleCommand : Command("toggle", "Toggle a module") {
    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.literal(name) {
            argument("module", word()) {
                suggestList { ModuleManager.getModules().map { it.name } }
                execute { context ->
                    val moduleName = context.getString("module")
                    val module = ModuleManager.getModuleByName(moduleName)
                    if (module != null) {
                        module.toggle()
                    } else {
                        MessageTool.error("Module not found: $moduleName")
                    }
                    1
                }
            }
        }
    }
}
