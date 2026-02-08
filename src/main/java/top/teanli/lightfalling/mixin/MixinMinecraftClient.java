package top.teanli.lightfalling.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.accessor.IMinecraftClient;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.TickEvent;
import top.teanli.lightfalling.tool.Browser;

@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient implements IMinecraftClient {

    @Override
    @Accessor("missTime")
    public abstract void setItemUseCooldown(int itemUseCooldown);

    @Override
    @Accessor("missTime")
    public abstract int getItemUseCooldown();

    @Override
    @Invoker("startAttack")
    public abstract boolean invokeDoAttack();

    @Override
    @Invoker("startUseItem")
    public abstract void invokeDoItemUse();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        EventManager.INSTANCE.post(new TickEvent());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        Browser.INSTANCE.download();
    }
}
