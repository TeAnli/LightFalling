package top.teanli.lightfalling

import net.ccbluex.liquidbounce.mcef.MCEF
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import top.teanli.lightfalling.command.CommandSystem
import top.teanli.lightfalling.module.ModuleManager
import top.teanli.lightfalling.tool.ToolManager

class Lightfalling : ModInitializer {
    private val log = LogManager.getLogger("Lightfalling")

    override fun onInitialize() {
//        if (MCEF.INSTANCE.initialize()) {
//            log.info("MCEF initialized")
//        } else {
//            log.error("Failed to initialize MCEF")
//        }
        log.info("Initializing lightfalling")
        ToolManager.init()
        ModuleManager.init()
        log.info("Module loaded")
        CommandSystem.init()
        log.info("Command loaded")
    }
}
