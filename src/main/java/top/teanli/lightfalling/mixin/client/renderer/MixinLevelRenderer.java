package top.teanli.lightfalling.mixin.client.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.Render3DEvent;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {

    @Inject(method = "renderLevel", at = @At(value = "RETURN"))
    private void beforeReturn(GraphicsResourceAllocator graphicsResourceAllocator, DeltaTracker deltaTracker, boolean bl,
                              Camera camera, Matrix4f positionMatrix, Matrix4f modelViewMatrix, Matrix4f matrix4f3,
                              GpuBufferSlice gpuBufferSlice, Vector4f vector4f, boolean bl2, CallbackInfo ci) {

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(positionMatrix);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        EventManager.INSTANCE
                .post(new Render3DEvent(camera, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), deltaTracker.getGameTimeDeltaPartialTick(false)));

        // Flush the buffer to ensure rendering
        bufferSource.endBatch();
    }
}

