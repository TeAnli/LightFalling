package top.teanli.lightfalling.command.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import top.teanli.lightfalling.Lightfalling
import top.teanli.lightfalling.command.Command
import top.teanli.lightfalling.command.argument
import top.teanli.lightfalling.command.execute
import top.teanli.lightfalling.command.getString
import top.teanli.lightfalling.command.literal
import top.teanli.lightfalling.command.suggestList
import top.teanli.lightfalling.command.word
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.MessageTool

class KeyCommand : Command("key", "Manage module keybinds") {
    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.literal(name) {
            argument("module", word()) {
                suggestList {
                    val modules = ModuleManager.getModules()
                    modules.map { it.name }
                }
                argument("key", word()) {
                    execute { context ->
                        val moduleName = context.getString("module")
                        val keyName = context.getString("key")

                        val module = ModuleManager.getModuleByName(moduleName)
                        if (module != null) {
                            val keyCode = when (keyName.uppercase()) {
                                "NONE" -> -1
                                else -> try {
                                    val field = Class.forName("org.lwjgl.glfw.GLFW").getField("GLFW_KEY_${keyName.uppercase()}")
                                    field.getInt(null)
                                } catch (e: Exception) {
                                    MessageTool.error("Invalid key name: $keyName")
                                    Lightfalling.log.error(e)
                                    return@execute 1
                                }
                            }
                            module.key = keyCode
                            MessageTool.info("Set keybind for ${module.name} to $keyName")
                        } else {
                            MessageTool.error("Module not found: $moduleName")
                        }
                        1
                    }
                }
            }
        }
    }
}