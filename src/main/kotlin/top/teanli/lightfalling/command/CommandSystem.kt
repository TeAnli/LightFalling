package top.teanli.lightfalling.command

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.tool.MessageTool
import top.teanli.lightfalling.tool.PackageScanner
import top.teanli.lightfalling.command.execute

/**
 * Command Manager
 * Responsible for registering and managing client-side commands.
 */
object CommandSystem {
    private val log = LogManager.getLogger("Lightfalling")
    private val commands = mutableListOf<Command>()

    /**
     * Initializes the command manager and registers commands.
     */
    fun init() {
        scanCommands()
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            registerCommands(dispatcher)
        }
    }

    /**
     * Automatically scans and registers command classes.
     */
    private fun scanCommands() {
        val packageName = "${javaClass.`package`.name}.impl"
        val classes = PackageScanner.scan(packageName, Command::class.java)

        for (cmdClass in classes) {
            try {
                val instance = instantiateCommand(cmdClass)
                if (instance != null) {
                    commands.add(instance)
                    log.info("Registered command: ${instance.name}")
                }
            } catch (e: Throwable) {
                log.error("Failed to load command: ${cmdClass.name} (${e.javaClass.name}: ${e.message})")
            }
        }
    }

    /**
     * Instantiates a command class, handling both Kotlin objects and regular classes.
     */
    private fun instantiateCommand(clazz: Class<out Command>): Command? {
        try {
            val field = clazz.getDeclaredField("INSTANCE")
            val instance = field.get(null)
            if (instance is Command) return instance
        } catch (e: Exception) {
            // Not a Kotlin object
        }

        return try {
            clazz.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            log.error("Failed to instantiate command: ${clazz.name}")
            null
        }
    }

    /**
     * Registers all scanned commands.
     */
    private fun registerCommands(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val root = ClientCommandManager.literal("lf")

        // Build each command into the root builder
        commands.forEach { it.build(root) }

        // Default execution logic
        root.execute {
            MessageTool.info("Use ${Formatting.BLUE}/lf toggle <module>${Formatting.WHITE} to toggle modules.")
            1
        }

        dispatcher.register(root)
        // Also register an alias /lightfalling
        dispatcher.register(ClientCommandManager.literal("lightfalling").redirect(dispatcher.root.getChild("lf")))
    }
}
