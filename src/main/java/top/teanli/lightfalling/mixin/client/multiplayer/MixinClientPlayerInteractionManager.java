package top.teanli.lightfalling.mixin.client.multiplayer;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.teanli.lightfalling.event.EventManager;
import top.teanli.lightfalling.event.impl.AttackEvent;
import top.teanli.lightfalling.event.impl.ClickBlockEvent;

@Mixin(MultiPlayerGameMode.class)
public class MixinClientPlayerInteractionManager {
    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        if (target != null) {
            EventManager.INSTANCE.post(new AttackEvent(target));
        }
    }

    @Inject(method = "startDestroyBlock", at = @At("HEAD"))
    private void onAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        EventManager.INSTANCE.post(new ClickBlockEvent(pos, direction));
    }
}
