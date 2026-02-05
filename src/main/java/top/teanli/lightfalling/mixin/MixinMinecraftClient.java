package top.teanli.lightfalling.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.TickEvent;
import top.teanli.lightfalling.accessor.IMinecraftClient;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClient {

    @Override
    @Accessor("itemUseCooldown")
    public abstract void setItemUseCooldown(int itemUseCooldown);

    @Override
    @Accessor("itemUseCooldown")
    public abstract int getItemUseCooldown();

    @Override
    @Invoker("doAttack")
    public abstract boolean invokeDoAttack();

    @Override
    @Invoker("doItemUse")
    public abstract void invokeDoItemUse();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        EventManager.INSTANCE.post(new TickEvent());
    }
}
