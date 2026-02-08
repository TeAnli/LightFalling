package top.teanli.lightfalling.module.modules.world

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Vec3d
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory

class DamageIndicator : Module(
    "DamageIndicator",
    "shows floating damage numbers above entities.",
    ModuleCategory.WORLD
) {
    private val lifeTime = slider("duration", 1000.0, 500.0, 3000.0, 0)
    private val size = slider("size", 1.0, 0.5, 3.0, 1)
    private val animationType = mode("animation", "Normal", listOf("Normal", "Scale"))

    private data class Damage(
        val amount: String,
        var pos: Vec3d, // Absolute world position
        val startTime: Long
    )

    private val damages = mutableListOf<Damage>()
    private val lastHealth = mutableMapOf<Int, Float>()

    private fun addDamage(entity: Entity, damage: Float) {
        // Initial position above the entity's head in world coordinates
        val pos = entity.entityPos.add(0.0, (entity as? LivingEntity)?.height?.toDouble() ?: 1.0, 0.0)
        damages.add(Damage(damage.toInt().toString(), pos, System.currentTimeMillis()))
    }

    private val onTick = listen<TickEvent> {
        val world = mc.world ?: return@listen
        val time = System.currentTimeMillis()

        // Remove expired damages and move them upward slightly
        damages.removeIf { time - it.startTime > lifeTime.value }
        damages.forEach { it.pos = it.pos.add(0.0, 0.015, 0.0) }

        // Detect damage by comparing current health to last known health
        world.entities.forEach { entity ->
            if (entity is LivingEntity) {
                val last = lastHealth[entity.id]
                val current = entity.health

                if (last != null && current < last) {
                    val damage = last - current
                    if (damage > 0f) addDamage(entity, damage)
                }
                lastHealth[entity.id] = current
            }
        }
    }

    private val onRender3D = listen<Render3DEvent> { event ->
        if (damages.isEmpty()) return@listen

        val timeNow = System.currentTimeMillis()
        val matrixStack = event.matrixStack
        val camera = event.camera
        val cameraPos = camera.cameraPos
        val textRenderer: TextRenderer = mc.textRenderer

        damages.forEach { damage ->
            val elapsed = timeNow - damage.startTime
            
            // compute fade alpha
            val alpha = (255 * (1.0 - (elapsed.toDouble() / lifeTime.value))).toInt().coerceIn(0, 255)
            val color = (alpha shl 24) or 0xFF0000 // Red color for damage

            matrixStack.push()

            // Translate from world coordinates to camera-relative coordinates
            val x = damage.pos.x - cameraPos.x
            val y = damage.pos.y - cameraPos.y
            val z = damage.pos.z - cameraPos.z
            matrixStack.translate(x.toFloat(), y.toFloat(), z.toFloat())

            // Billboard rotation (facing camera)
            matrixStack.multiply(camera.rotation)

            // Apply scale
            var scaleF = (0.02f * size.value.toFloat()).coerceAtLeast(0.001f)
            
            // Apply scale animation
            if (animationType.value == "Scale") {
                val progress = elapsed.toDouble() / lifeTime.value
                // Simple pop-out and shrink effect
                val animationScale = if (progress < 0.2) {
                    (progress / 0.2) * 1.2 // Pop up to 1.2x in first 20% of lifetime
                } else {
                    1.2 - ((progress - 0.2) / 0.8) * 0.2 // Shrink back to 1.0x over remaining 80%
                }
                scaleF *= animationScale.toFloat()
            }

            matrixStack.scale(scaleF, -scaleF, scaleF)

            // Center text
            val text = damage.amount
            val textWidth = textRenderer.getWidth(text)
            
            textRenderer.draw(
                text,
                -textWidth / 2.0f,
                0.0f,
                color,
                true,
                matrixStack.peek().positionMatrix,
                event.vertexConsumer,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            )

            matrixStack.pop()
        }
    }

    override fun onDisable() {
        damages.clear()
        lastHealth.clear()
    }
}
