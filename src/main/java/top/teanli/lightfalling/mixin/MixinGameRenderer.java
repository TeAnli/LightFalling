package top.teanli.lightfalling.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.module.modules.render.CustomFOV;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(CallbackInfoReturnable<Float> cir) {
        if (CustomFOV.INSTANCE.getState()) {
            cir.setReturnValue(CustomFOV.INSTANCE.getFov().getValue().floatValue());
        }
    }
}
