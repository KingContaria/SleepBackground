package com.redlimerl.sleepbackground.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redlimerl.sleepbackground.SleepBackground;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = {"clear", "enableCull", "enableTexture"}, at = @At("HEAD"), cancellable = true)
    private static void cancelRenderSystem(CallbackInfo ci) {
        if (SleepBackground.shouldNotRender) ci.cancel();
    }

    @Inject(method = {"fogDensity", "fogMode(Lcom/mojang/blaze3d/platform/GlStateManager$FogMode;)V"}, at = @At("HEAD"), cancellable = true, require = 0)
    private static void cancelRenderSystem_notRequired(CallbackInfo ci) {
        if (SleepBackground.shouldNotRender) ci.cancel();
    }
}
