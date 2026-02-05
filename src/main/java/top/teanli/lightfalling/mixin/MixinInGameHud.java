package top.teanli.lightfalling.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.Render2DEvent;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        EventManager.INSTANCE.post(new Render2DEvent(context, tickCounter.getTickProgress(true)));
    }
}
