package top.teanli.lightfalling.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.MessageTool

/**
 * Command Manager
 * Responsible for registering and managing client-side commands.
 */
object CommandManager {
    private val log = LogManager.getLogger("Lightfalling")
    /**
     * Initializes the command manager and registers commands.
     */
    fun init() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            registerCommands(dispatcher)
        }
    }

    /**
     * Registers all client-side commands.
     */
    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        // Main command: /lf or /lightfalling
        val root = ClientCommandManager.literal("lf")
            .then(ClientCommandManager.literal("toggle")
                .then(ClientCommandManager.argument("module", StringArgumentType.word())
                    .suggests { _, builder ->
                        ModuleManager.getModules().forEach { builder.suggest(it.name) }
                        builder.buildFuture()
                    }
                    .executes { context ->
                        val moduleName = StringArgumentType.getString(context, "module")
                        val module = ModuleManager.getModuleByName(moduleName)
                        if (module != null) {
                            module.toggle()
                        } else {
                            MessageTool.error("Module not found: $moduleName")
                        }
                        1
                    }
                )
            )
            .then(ClientCommandManager.literal("list")
                .executes { _ ->
                    val modules = ModuleManager.getModules().joinToString(", ") {
                        if (it.state) "${Formatting.GREEN}${it.name}" else "${Formatting.RED}${it.name}"
                    }
                    MessageTool.info("Modules: $modules")
                    1
                }
            )
            .executes { context ->
                MessageTool.info("Use ${Formatting.BLUE}/lf toggle <module>${Formatting.WHITE} to toggle modules.")
                1
            }

        dispatcher.register(root)
        // Also register an alias /lightfalling
        dispatcher.register(ClientCommandManager.literal("lightfalling").redirect(dispatcher.root.getChild("lf")))
    }
}
