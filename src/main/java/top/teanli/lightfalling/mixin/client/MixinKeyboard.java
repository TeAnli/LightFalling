package top.teanli.lightfalling.mixin.client;

import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.KeyEvent;

@Mixin(KeyboardHandler.class)
public class MixinKeyboard {
    @Inject(method = "keyPress", at = @At("HEAD"))
    private void onKey(long window, int i, net.minecraft.client.input.KeyEvent keyEvent, CallbackInfo ci) {
        EventManager.INSTANCE.post(new KeyEvent(
            keyEvent.key(),
            keyEvent.scancode(), i,
            keyEvent.modifiers()
        ));
    }
}
