package top.teanli.lightfalling.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.state.TntEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.TNTRenderEvent;
import top.teanli.lightfalling.module.ModuleManager;
import top.teanli.lightfalling.module.modules.world.TNTTimer;

@Mixin(TntEntityRenderer.class)
public class MixinTNTEntityRenderer {
    @Inject(method = "render*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionfc;)V",
                    shift = At.Shift.AFTER,
                    ordinal = 1
            )
    )
    public void render(TntEntityRenderState tntEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo callbackInfo) {
        EventManager.INSTANCE.post(new TNTRenderEvent(tntEntityRenderState, matrixStack));
    }
}
