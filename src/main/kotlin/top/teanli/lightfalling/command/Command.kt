package top.teanli.lightfalling.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import org.apache.logging.log4j.LogManager

/**
 * Base class for all client-side commands.
 */
abstract class Command(
    val name: String,
    val description: String
) {
    protected val log = LogManager.getLogger("LightFalling")
    /**
     * Builds the command structure.
     */
    abstract fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>)
}
