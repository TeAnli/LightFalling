package top.teanli.lightfalling.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.module.modules.player.BrightnessChanger;
import top.teanli.lightfalling.module.modules.player.CustomFOV;

@Mixin(SimpleOption.class)
public class MixinSimpleOption<T> {
    @SuppressWarnings("unchecked")
    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void onGetValue(CallbackInfoReturnable<T> cir) {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().options == null)
            return;

        if (this == (Object) MinecraftClient.getInstance().options.getGamma()) {
            if (BrightnessChanger.INSTANCE.getState()
                    && BrightnessChanger.INSTANCE.getMode().getValue().equals("Gamma")) {
                cir.setReturnValue((T) BrightnessChanger.INSTANCE.getBrightness().getValue());
            }
        } else if (this == (Object) MinecraftClient.getInstance().options.getFov()) {
            if (CustomFOV.INSTANCE.getState()) {
                cir.setReturnValue((T) Integer.valueOf(CustomFOV.INSTANCE.getFov().getValue().intValue()));
            }
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"))
    private void onSetValue(T value, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().options == null)
            return;

        if (this == (Object) MinecraftClient.getInstance().options.getFov()) {
            if (CustomFOV.INSTANCE.getState() && value instanceof Integer) {
                CustomFOV.INSTANCE.getFov().setValue(((Integer) value).doubleValue());
            }
        }
    }
}
