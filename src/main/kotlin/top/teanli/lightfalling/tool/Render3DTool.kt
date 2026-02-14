package top.teanli.lightfalling.tool

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.phys.Vec3
import java.awt.Color

/**
 * Utility object for 3D world rendering
 */
object Render3DTool {
    private val mc: Minecraft = Minecraft.getInstance()

    /**
     * Renders text in 3D world space at the specified position
     * 
     * @param poseStack The pose stack for transformations
     * @param buffer The buffer source for rendering
     * @param cameraPos The camera position
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param z World Z coordinate
     * @param text The text to render
     * @param color The color of the text (RGB)
     * @param scale Base scale factor (default: 0.025f)
     * @param distanceScale Whether to scale based on distance (default: false)
     * @param displayMode Font display mode (default: SEE_THROUGH)
     * @param shadow Whether to render shadow (default: true)
     */
    fun renderText3D(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        cameraPos: Vec3,
        x: Double,
        y: Double,
        z: Double,
        text: String,
        color: Int,
        scale: Float = 0.025f,
        distanceScale: Boolean = false,
        displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
        shadow: Boolean = true
    ) {
        val font: Font = mc.font
        val camera = mc.gameRenderer.mainCamera

        poseStack.pushPose()

        // Calculate position relative to camera
        val relX = x - cameraPos.x
        val relY = y - cameraPos.y
        val relZ = z - cameraPos.z

        poseStack.translate(relX, relY, relZ)
        poseStack.mulPose(camera.rotation())

        // Calculate scale
        val finalScale = if (distanceScale) {
            val distance = Math.sqrt(relX * relX + relY * relY + relZ * relZ).toFloat()
            scale * Math.max(1.0f, distance * 0.15f)
        } else {
            scale
        }
        
        poseStack.scale(finalScale, -finalScale, finalScale)

        // Calculate text width for centering
        val width = font.width(text)
        val colorInt = color or (255 shl 24)

        // Render text
        font.drawInBatch(
            text,
            -width / 2f,
            0f,
            colorInt,
            shadow,
            poseStack.last().pose(),
            buffer,
            displayMode,
            0,
            LightTexture.FULL_BRIGHT
        )

        poseStack.popPose()
    }

    /**
     * Renders text in 3D world space with Color object
     */
    fun renderText3D(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        cameraPos: Vec3,
        x: Double,
        y: Double,
        z: Double,
        text: String,
        color: Color,
        scale: Float = 0.025f,
        distanceScale: Boolean = false,
        displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
        shadow: Boolean = true
    ) {
        renderText3D(poseStack, buffer, cameraPos, x, y, z, text, color.rgb, scale, distanceScale, displayMode, shadow)
    }
}
