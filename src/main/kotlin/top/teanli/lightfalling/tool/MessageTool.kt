package top.teanli.lightfalling.tool

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/**
 * Utility for sending messages to the player's chat.
 */
object MessageTool {
    private val prefix = "${Formatting.GRAY}[${Formatting.BLUE}LightFalling${Formatting.GRAY}] "

    /**
     * Sends a raw message to the chat.
     */
    fun sendRaw(message: String) {
        MinecraftClient.getInstance().player?.sendMessage(Text.literal(message), false)
    }

    /**
     * Sends an informational message with the mod prefix.
     */
    fun info(message: String) {
        sendRaw(prefix + message)
    }

    /**
     * Sends an error message with the mod prefix and red color.
     */
    fun error(message: String) {
        sendRaw(prefix + "${Formatting.RED}Error: $message")
    }

    /**
     * Sends a debug message (only if debug mode is enabled, or just plain info for now).
     */
    fun debug(message: String) {
        info("${Formatting.YELLOW}[DEBUG] $message")
    }
}
