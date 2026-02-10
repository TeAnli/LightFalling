package top.teanli.lightfalling.mixin.player;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.MotionEvent;

@Mixin(LocalPlayer.class)
public class MixinClientPlayerEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(CallbackInfo info) {
        EventManager.INSTANCE.post(new MotionEvent(MotionEvent.Stage.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTickTail(CallbackInfo info) {
        EventManager.INSTANCE.post(new MotionEvent(MotionEvent.Stage.POST));
    }
}
