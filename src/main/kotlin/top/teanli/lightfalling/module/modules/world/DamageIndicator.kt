package top.teanli.lightfalling.module.modules.world

import net.minecraft.client.gui.Font
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import top.teanli.lightfalling.event.impl.Render3DEvent
import top.teanli.lightfalling.event.impl.TickEvent
import top.teanli.lightfalling.event.listen
import top.teanli.lightfalling.module.Module
import top.teanli.lightfalling.module.ModuleCategory
import kotlin.math.pow

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
        var pos: Vec3, // Absolute world position
        val startTime: Long
    )

    private val damages = mutableListOf<Damage>()
    private val lastHealth = mutableMapOf<Int, Float>()

    private fun addDamage(entity: Entity, damage: Float) {
        // Initial position above the entity's head in world coordinates
        val pos = entity.position().add(0.0, (entity as? LivingEntity)?.bbHeight?.toDouble() ?: 1.0, 0.0)
        damages.add(Damage(damage.toInt().toString(), pos, System.currentTimeMillis()))
    }

    private val onTick = listen<TickEvent> {
        val level = mc.level ?: return@listen
        val time = System.currentTimeMillis()

        // Remove expired damages and move them upward slightly
        damages.removeIf { time - it.startTime > lifeTime.value }
        if (animationType.value != "Scale") {
            damages.forEach { it.pos = it.pos.add(0.0, 0.015, 0.0) }
        }
        // Detect damage by comparing current health to last known health
        level.entitiesForRendering()
            .forEach { entity ->
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
        val poseStack = event.poseStack
        val camera = event.camera
        val cameraPos = camera.position()
        val font: Font = mc.font

        damages.forEach { damage ->
            val elapsed = timeNow - damage.startTime
            
            // compute fade alpha
            val alpha = (255 * (1.0 - (elapsed.toDouble() / lifeTime.value))).toInt().coerceIn(0, 255)
            val color = (alpha shl 24) or 0xFF0000 // Red color for damage

            poseStack.pushPose()

            // Translate from world coordinates to camera-relative coordinates
            val x = damage.pos.x - cameraPos.x
            val y = damage.pos.y - cameraPos.y
            val z = damage.pos.z - cameraPos.z
            poseStack.translate(x.toFloat(), y.toFloat(), z.toFloat())

            // Billboard rotation (facing camera)
            poseStack.mulPose(camera.rotation())

            // Apply scale
            var scaleF = (0.02f * size.value.toFloat()).coerceAtLeast(0.001f)
            
            // Apply scale animation
            if (animationType.value == "Scale") {
                val animDurationRatio = 0.3
                val progress = (elapsed.toDouble() / (lifeTime.value * animDurationRatio)).coerceIn(0.0, 1.0)
                val startScale = 0.0
                val endScale = 1.0
                val eased = 1.0 - (1.0 - progress).pow(2.0)
                val animationScale = startScale + (endScale - startScale) * eased
                scaleF *= animationScale.toFloat()
            }

            poseStack.scale(-scaleF, -scaleF, scaleF)

            // Draw text centered
            val text = damage.amount
            val width = font.width(text)
            font.drawInBatch(text, -width / 2f, 0f, color, true, poseStack.last().pose(), mc.renderBuffers().bufferSource(), net.minecraft.client.gui.Font.DisplayMode.NORMAL, 0, 15728880)

            poseStack.popPose()
        }
    }
}
