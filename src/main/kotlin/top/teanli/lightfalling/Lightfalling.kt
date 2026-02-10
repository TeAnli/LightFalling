package top.teanli.lightfalling

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.command.CommandSystem
import top.teanli.lightfalling.config.ConfigSystem
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.ToolManager
import top.teanli.lightfalling.ui.clickgui.ClickGUIScreen

class Lightfalling : ModInitializer {
    companion object{
        val log: Logger = LogManager.getLogger("Lightfalling")
    }

    var category: KeyMapping.Category = KeyMapping.Category(Identifier.fromNamespaceAndPath("", ""))
    var clickGUI = KeyBindingHelper.registerKeyBinding(
        KeyMapping(
            "key.lightfalling.clickgui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            category
        )
    )

    override fun onInitialize() {
        log.info("Initializing lightfalling")
        ToolManager.init()
        ModuleManager.init()
        log.info("Module and ModuleManager loaded")
        ConfigSystem.load()
        log.info("Configuration loaded")
        CommandSystem.init()
        log.info("Command and CommandSystem loaded")

        ClientTickEvents.END_CLIENT_TICK.register {
            while (clickGUI.consumeClick()) {
                Minecraft.getInstance().setScreen(ClickGUIScreen())
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            ConfigSystem.save()
        })
    }
}
