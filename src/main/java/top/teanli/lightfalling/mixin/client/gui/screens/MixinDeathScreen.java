package top.teanli.lightfalling.mixin.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.teanli.lightfalling.module.modules.player.DeathPoint;
import top.teanli.lightfalling.tool.I18n;

@Mixin(DeathScreen.class)
public abstract class MixinDeathScreen extends Screen {

    protected MixinDeathScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        BlockPos deathPos = DeathPoint.Companion.getLastDeathPos();
        if (deathPos != null) {
            // 添加传送按钮
            this.addRenderableWidget(
                    Button.builder(I18n.INSTANCE.component("lightfalling.gui.deathscreen.tp"), (button) -> {
                        if (this.minecraft != null && this.minecraft.player != null) {
                            this.minecraft.player.connection
                                    .sendChat("/tp " + deathPos.getX() + " " + deathPos.getY() + " " + deathPos.getZ());
                        }
                    }).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build());
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        BlockPos deathPos = DeathPoint.Companion.getLastDeathPos();
        if (deathPos != null) {
            String coordsStr = String.format("X: %d, Y: %d, Z: %d", deathPos.getX(), deathPos.getY(), deathPos.getZ());
            Component coords = I18n.INSTANCE.component("lightfalling.gui.deathscreen.coords", coordsStr);
            guiGraphics.drawCenteredString(this.font, coords, this.width / 2, this.height / 4 + 100, 0xFFFFFF);
        }
    }
}
