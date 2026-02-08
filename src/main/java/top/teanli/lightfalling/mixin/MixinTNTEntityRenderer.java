package top.teanli.lightfalling.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.TNTRenderEvent;

@Mixin(TntRenderer.class)
public class MixinTNTEntityRenderer {
    @Inject(method = "submit*", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionfc;)V", shift = At.Shift.AFTER, ordinal = 1))
    public void render(TntRenderState tntEntityRenderState, PoseStack matrixStack, Object buffer, int packedLight,
                       CallbackInfo callbackInfo) {
        EventManager.INSTANCE.post(new TNTRenderEvent(tntEntityRenderState, matrixStack));
    }
}
