package top.teanli.lightfalling

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import top.teanli.lightfalling.command.CommandSystem
import top.teanli.lightfalling.config.ConfigSystem
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.ToolManager

class Lightfalling : ModInitializer {
    companion object{
        val log: Logger = LogManager.getLogger("Lightfalling")
    }

    override fun onInitialize() {
        log.info("Initializing lightfalling")
        ToolManager.init()
        ModuleManager.init()
        ConfigSystem.load()
        Runtime.getRuntime().addShutdownHook(Thread {
            ConfigSystem.save()
        })
        log.info("Module loaded")
        ConfigSystem.load()
        CommandSystem.init()
        log.info("Command loaded")

    }

}
