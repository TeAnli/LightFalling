package top.teanli.lightfalling.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.Lightfalling
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.module.setting.*
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Configuration System
 * Handles saving and loading module states and settings using JSON.
 */
object ConfigSystem {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configDir: File = FabricLoader.getInstance().configDir.resolve("LightFalling").toFile()
    private val configFile: File = configDir.resolve("config.json")
    init {
        if (!configDir.exists()) {
            configDir.mkdirs()
        }
    }

    /**
     * Saves the current configuration to a file.
     */
    fun save() {
        val rootObject = JsonObject()
        val modulesObject = JsonObject()

        ModuleManager.getModules().forEach { module ->
            val moduleObject = JsonObject()
            moduleObject.addProperty("enabled", module.state)

            val settingsObject = JsonObject()
            module.settings.forEach { setting ->
                when (setting) {
                    is BooleanSetting -> settingsObject.addProperty(setting.name, setting.value)
                    is NumberSetting -> settingsObject.addProperty(setting.name, setting.value)
                    is ModeSetting -> settingsObject.addProperty(setting.name, setting.value)
                    is ColorSetting -> settingsObject.addProperty(setting.name, setting.value.rgb)
                }
            }
            moduleObject.add("settings", settingsObject)
            modulesObject.add(module.name, moduleObject)
        }

        rootObject.add("modules", modulesObject)

        try {
            configFile.writeText(gson.toJson(rootObject), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Loads the configuration from the file.
     */
    fun load() {
        if (!configFile.exists()) return

        try {
            val rootObject = gson.fromJson(configFile.readText(StandardCharsets.UTF_8), JsonObject::class.java)
            val modulesObject = rootObject.getAsJsonObject("modules") ?: return

            ModuleManager.getModules().forEach { module ->
                val moduleObject = modulesObject.getAsJsonObject(module.name) ?: return@forEach

                // Load state
                val enabled = moduleObject.get("enabled")?.asBoolean ?: false
                if (enabled != module.state) {
                    module.toggle()
                }

                // Load settings
                val settingsObject = moduleObject.getAsJsonObject("settings") ?: return@forEach
                module.settings.forEach { setting ->
                    val settingElement = settingsObject.get(setting.name) ?: return@forEach
                    try {
                        when (setting) {
                            is BooleanSetting -> setting.value = settingElement.asBoolean
                            is NumberSetting -> setting.value = settingElement.asDouble
                            is ModeSetting -> {
                                val mode = settingElement.asString
                                if (setting.modes.contains(mode)) {
                                    setting.value = mode
                                }
                            }
                            is ColorSetting -> setting.value = java.awt.Color(settingElement.asInt, true)
                        }
                    } catch (e: Exception) {
                        // Skip invalid settings
                        Lightfalling.log.error("Error setting value: $setting", e)
                    }
                }
            }
        } catch (e: Exception) {
            Lightfalling.log.error("Error loading config", e)
        }
    }
}
