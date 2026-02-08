package top.teanli.lightfalling.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.Render3DEvent;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
    public void hookWorldRender(
            DeltaTracker deltaTracker,
            CallbackInfo ci,
            @Local(ordinal = 0) Matrix4f projectionMatrix,
            @Local(ordinal = 1) Matrix4f modelViewMatrix
    ) {
        PoseStack poseStack = new PoseStack();
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.set(projectionMatrix).mul(modelViewMatrix);
        poseStack.mulPose(matrix4f);
        EventManager.INSTANCE.post(new Render3DEvent(this.mainCamera, poseStack, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }
}
