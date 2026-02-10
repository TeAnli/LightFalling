package top.teanli.lightfalling.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.ChatFormatting
import top.teanli.lightfalling.command.impl.HelpCommand
import top.teanli.lightfalling.command.impl.KeyCommand
import top.teanli.lightfalling.command.impl.ListCommand
import top.teanli.lightfalling.command.impl.SetCommand
import top.teanli.lightfalling.command.impl.ToggleCommand
import top.teanli.lightfalling.tool.MessageTool

/**
 * Command Manager
 * Responsible for registering and managing client-side commands.
 * @author TeAnli
 */
object CommandSystem {
    private val commands = mutableListOf<Command>()

    fun getCommands(): List<Command> = commands

    /**
     * Initializes the command manager and registers commands.
     */
    fun init() {
        commands.addAll(listOf(
            HelpCommand(),
            KeyCommand(),
            ListCommand(),
            SetCommand(),
            ToggleCommand()
        ))
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            registerCommands(dispatcher)
        }
    }

    /**
     * Registers all scanned commands.
     */
    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val root = ClientCommandManager.literal("lf")
        // Build each command into the root builder
        commands.forEach { it.build(root) }
        root.execute {
            MessageTool.info("Use ${ChatFormatting.BLUE}/lf help ${ChatFormatting.WHITE} to see available commands details.")
            1
        }
        dispatcher.register(root)
        // register alias "lightfalling"
        dispatcher.register(ClientCommandManager.literal("lightfalling").redirect(dispatcher.root.getChild("lf")))
    }
}
