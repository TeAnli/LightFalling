package top.teanli.lightfalling

import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import org.apache.logging.log4j.LogManager
import org.lwjgl.glfw.GLFW
import top.teanli.lightfalling.command.CommandSystem
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.ToolManager
import top.teanli.lightfalling.ui.web.WebUI
import top.teanli.lightfalling.ui.web.WebUIScreen

class Lightfalling : ModInitializer {
    private val log = LogManager.getLogger("Lightfalling")
    var category: KeyMapping.Category = KeyMapping.Category(
        Identifier.fromNamespaceAndPath("test", "custom_category")
    )
    var browserGUI = KeyBindingHelper.registerKeyBinding(
        KeyMapping(
                "key.example-mod.send_to_chat", // The translation key for the key mapping.
            InputConstants.Type.KEYSYM, // // The type of the keybinding; KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_J, // The GLFW keycode of the key.
        category // The category of the mapping.
    ))

    override fun onInitialize() {


        log.info("Initializing lightfalling")
        ToolManager.init()

        ModuleManager.init()
        log.info("Module loaded")
        CommandSystem.init()
        log.info("Command loaded")

        ClientTickEvents.END_CLIENT_TICK.register( { client ->
            while (browserGUI.consumeClick()) {
                val sampleWebUI = WebUI("https://www.google.com/")
                Minecraft.getInstance().setScreen(WebUIScreen(sampleWebUI))
            }

        });
    }

}
