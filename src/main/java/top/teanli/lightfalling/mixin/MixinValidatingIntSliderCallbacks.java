package top.teanli.lightfalling.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.module.modules.render.CustomFOV;

@Mixin(targets = "net.minecraft.client.option.SimpleOption$ValidatingIntSliderCallbacks")
public class MixinValidatingIntSliderCallbacks {

    @Inject(method = "maxInclusive", at = @At("RETURN"), cancellable = true)
    private void onMaxInclusive(CallbackInfoReturnable<Integer> cir) {
        if (CustomFOV.INSTANCE.getState() && cir.getReturnValue() == 110) {
            cir.setReturnValue(160);
        }
    }
}
