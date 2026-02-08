package top.teanli.lightfalling.tool

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

/**
 * Utility for sending messages to the player's chat.
 */
object MessageTool {
    private val prefix = "${ChatFormatting.GRAY}[${ChatFormatting.BLUE}LightFalling${ChatFormatting.GRAY}] "

    /**
     * Sends a raw message to the chat.
     */
    fun sendRaw(message: String) {
        Minecraft.getInstance().player?.displayClientMessage(Component.literal(message), false)
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
        sendRaw(prefix + "${ChatFormatting.RED}Error: $message")
    }

    /**
     * Sends a debug message (only if debug mode is enabled, or just plain info for now).
     */
    fun debug(message: String) {
        info("${ChatFormatting.YELLOW}[DEBUG] $message")
    }
}
