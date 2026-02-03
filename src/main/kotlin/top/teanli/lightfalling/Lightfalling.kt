package top.teanli.lightfalling

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.command.CommandManager
import top.teanli.lightfalling.module.ModuleManager

class Lightfalling : ModInitializer {
    private val log = LogManager.getLogger("Lightfalling")

    override fun onInitialize() {
        log.info("Initializing lightfalling")
        ModuleManager.init()
        log.info("Module loaded")
        CommandManager.init()
        log.info("Command loaded")
    }
}
