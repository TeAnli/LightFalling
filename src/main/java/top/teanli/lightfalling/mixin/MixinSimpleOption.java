package top.teanli.lightfalling.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.module.modules.player.BrightnessChanger;
import top.teanli.lightfalling.module.modules.player.CustomFOV;

@Mixin(OptionInstance.class)
public class MixinSimpleOption<T> {
    @SuppressWarnings("unchecked")
    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private void onGetValue(CallbackInfoReturnable<T> cir) {
        if (Minecraft.getInstance() == null || Minecraft.getInstance().options == null)
            return;

        if (this == (Object) Minecraft.getInstance().options.gamma()) {
            if (BrightnessChanger.INSTANCE.getState()
                    && BrightnessChanger.INSTANCE.getMode().getValue().equals("Gamma")) {
                cir.setReturnValue((T) BrightnessChanger.INSTANCE.getBrightness().getValue());
            }
        } else if (this == (Object) Minecraft.getInstance().options.fov()) {
            if (CustomFOV.INSTANCE.getState()) {
                cir.setReturnValue((T) Integer.valueOf(CustomFOV.INSTANCE.getFov().getValue().intValue()));
            }
        }
    }

    @Inject(method = "set", at = @At("HEAD"))
    private void onSetValue(T value, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (Minecraft.getInstance() == null || Minecraft.getInstance().options == null)
            return;

        if (this == (Object) Minecraft.getInstance().options.fov()) {
            if (CustomFOV.INSTANCE.getState() && value instanceof Integer) {
                CustomFOV.INSTANCE.getFov().setValue(((Integer) value).doubleValue());
            }
        }
    }
}
