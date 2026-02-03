package top.teanli.lightfalling.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.module.modules.render.BrightnessChanger;

@Mixin(SimpleOption.class)
public class MixinSimpleOption<T> {

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void onGetValue(CallbackInfoReturnable<T> cir) {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().options == null)
            return;

        SimpleOption<Double> gammaOption = MinecraftClient.getInstance().options.getGamma();
        if (this == (Object) gammaOption) {
            if (BrightnessChanger.INSTANCE.getState()
                    && BrightnessChanger.INSTANCE.getMode().getValue().equals("Gamma")) {
                cir.setReturnValue((T) Double.valueOf(BrightnessChanger.INSTANCE.getBrightness().getValue()));
            }
        }
    }
}
