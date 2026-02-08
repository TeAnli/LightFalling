package top.teanli.lightfalling.event.impl

import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.state.TntEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.network.packet.Packet
import org.joml.Matrix4f
import top.teanli.lightfalling.event.Event

/**
 * Event posted every client tick.
 */
class TickEvent : Event()

/**
 * Event posted before and after player motion updates.
 */
class MotionEvent(val stage: Stage) : Event() {
    enum class Stage {
        PRE, POST
    }
}

/**
 * Event posted when rendering the world in 3D.
 *
 * NOTE: Added `matrixStack` so modules can render using Minecraft's MatrixStack
 * (e.g. to draw text in world space). This is a non-breaking addition for Kotlin
 * consumers since a new parameter was appended and Mixin will supply it.
 */
class Render3DEvent(
    val camera: Camera,
    val vertexConsumer: VertexConsumerProvider.Immediate,
    val matrixStack: MatrixStack
) : Event()

/**
 * Event posted when rendering the HUD in 2D.
 */
class Render2DEvent(
    val drawContext: net.minecraft.client.gui.DrawContext,
    val tickDelta: Float
) : Event()

/**
 * Event posted during entity rendering.
 */
class RenderEntityEvent(
    val matrixStack: MatrixStack
) : Event()

/**
 * Event posted when the player attacks an entity.
 */
class AttackEvent(val target: net.minecraft.entity.Entity) : Event()

/**
 * Event posted when the player starts mining a block.
 */
class ClickBlockEvent(val pos: net.minecraft.util.math.BlockPos, val direction: net.minecraft.util.math.Direction) : Event()

/**
 * Event posted when an entity takes damage.
 */
class EntityDamageEvent(val entity: net.minecraft.entity.LivingEntity, val amount: Float) : Event()

/**
 * Event posted when a packet is sent or received.
 */
open class PacketEvent(val packet: Packet<*>) : Event() {
    class Receive(packet: Packet<*>) : PacketEvent(packet)
    class Send(packet: Packet<*>) : PacketEvent(packet)
}

class TNTRenderEvent(val tntEntityRenderState: TntEntityRenderState, val matrixStack: MatrixStack) : Event()