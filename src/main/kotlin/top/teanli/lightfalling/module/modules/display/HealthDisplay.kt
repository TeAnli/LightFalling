package top.teanli.lightfalling.module.modules.display

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import top.teanli.lightfalling.tool.ColorTool
import top.teanli.lightfalling.tool.EntityTool
import top.teanli.lightfalling.tool.Render3DTool

class HealthDisplay : Module(
    "HealthDisplay",
    "Displays health above entities",
    ModuleCategory.DISPLAY
) {
    private val showPlayers = checkbox("showplayers", true)
    private val showMobs = checkbox("showmobs", true)
    private val showAnimals = checkbox("showanimals", false)
    private val showPercentage = checkbox("showpercentage", true)
    private val showHearts = checkbox("showhearts", true)
    private val scanRange = slider("scanrange", 64.0, 16.0, 128.0, 0)

    private val onRender3D = listen<Render3DEvent> { event ->
        val player = mc.player ?: return@listen
        
        val range = scanRange.value
        val playerPos = player.position()
        
        // Scan for entities within range
        val entities = EntityTool.getEntitiesInRange(playerPos, range)
        for (entity in entities) {
            if (entity !is LivingEntity) continue
            if (entity == player) continue // Don't show for self
            
            // Filter by entity type
            when {
                entity is Player -> if (!showPlayers.value) continue
                EntityTool.isHostileMob(entity) -> if (!showMobs.value) continue
                EntityTool.isPassiveMob(entity) -> if (!showAnimals.value) continue
                else -> continue
            }
            
            // Check if entity is visible (line of sight)
            if (!EntityTool.canSeeEntity(player, entity)) continue
            
            // Get health info
            val health = entity.health
            val maxHealth = entity.maxHealth
            
            if (maxHealth <= 0) continue
            
            // Build health text
            val healthText = buildHealthText(health, maxHealth)
            
            // Get color based on health percentage
            val healthPercent = (health / maxHealth * 100).toInt()
            val color = ColorTool.getPercentageColor(healthPercent)
            
            Render3DTool.renderText3D(
                event.poseStack,
                event.buffer,
                event.camera.position(),
                entity.position().x,
                entity.position().y + entity.bbHeight + 0.3,
                entity.position().z,
                healthText,
                color
            )
        }
    }

    private fun buildHealthText(health: Float, maxHealth: Float): String {
        val parts = mutableListOf<String>()
        
        if (showHearts.value) {
            val hearts = String.format("%.1f", health / 2.0f)
            val maxHearts = String.format("%.1f", maxHealth / 2.0f)
            parts.add("❤ $hearts/$maxHearts")
        } else {
            parts.add(String.format("%.1f/%.1f", health, maxHealth))
        }
        
        if (showPercentage.value) {
            val percent = (health / maxHealth * 100).toInt()
            parts.add("($percent%)")
        }
        
        return parts.joinToString(" ")
    }
}
