package com.redlimerl.sleepbackground.mixin;

import com.redlimerl.sleepbackground.SleepBackground;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ToastManager.class)
public class MixinToastManager {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true, require = 0)
    private void cancelDraw(CallbackInfo ci) {
        if (SleepBackground.shouldNotRender) ci.cancel();
    }
}
