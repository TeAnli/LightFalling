package top.teanli.lightfalling.command.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import top.teanli.lightfalling.Lightfalling
import top.teanli.lightfalling.command.*
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.module.setting.*
import top.teanli.lightfalling.tool.MessageTool

object SetCommand : Command("set", "Modify module settings") {
    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.literal(name) {
            argument("module", word()) {
                suggestList { ModuleManager.getModules().map { it.name } }
                argument("setting", word()) {
                    suggestList { context ->
                        val moduleName = context.getString("module")
                        val module = ModuleManager.getModuleByName(moduleName)
                        module?.settings?.map { it.name.replace(" ", "_") } ?: emptyList()
                    }
                    argument("value", greedyString()) {
                        execute { context ->
                            val moduleName = context.getString("module")
                            val settingName = context.getString("setting").replace("_", " ")
                            val valueStr = context.getString("value")

                            val module = ModuleManager.getModuleByName(moduleName)
                            if (module == null) {
                                MessageTool.error("Module not found: $moduleName")
                                return@execute 1
                            }

                            val setting = module.settings.find { it.name.equals(settingName, ignoreCase = true) }
                            if (setting == null) {
                                MessageTool.error("Setting not found: $settingName")
                                return@execute 1
                            }

                            try {
                                when (setting) {
                                    is BooleanSetting -> {
                                        setting.value = valueStr.toBooleanStrict()
                                    }
                                    is NumberSetting -> {
                                        setting.value = valueStr.toDouble()
                                    }
                                    is ModeSetting -> {
                                        if (setting.modes.any { it.equals(valueStr, ignoreCase = true) }) {
                                            setting.value = setting.modes.find { it.equals(valueStr, ignoreCase = true) }!!
                                        } else {
                                            MessageTool.error("Invalid mode: $valueStr. Available: ${setting.modes.joinToString(", ")}")
                                            return@execute 1
                                        }
                                    }
                                    else -> {
                                        MessageTool.error("Setting type not supported via command yet.")
                                        return@execute 1
                                    }
                                }
                                MessageTool.info("Set ${setting.name} to $valueStr")
                            } catch (e: Exception) {
                                MessageTool.error("Invalid value: $valueStr")
                                Lightfalling.log.error("Error setting value: $valueStr", e)
                            }
                            1
                        }
                    }
                }
            }
        }
    }
}
